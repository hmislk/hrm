/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.bean.pharmacy;

import com.divudi.bean.common.CommonController;
import com.divudi.bean.common.SessionController;
import com.divudi.bean.common.UtilityController;
import com.divudi.data.BillClassType;
import com.divudi.data.BillNumberSuffix;
import com.divudi.data.BillType;
import com.divudi.ejb.BillNumberGenerator;
import com.divudi.ejb.PharmacyBean;
import com.divudi.ejb.PharmacyCalculation;
import com.divudi.entity.Bill;
import com.divudi.entity.BillItem;
import com.divudi.entity.BilledBill;
import com.divudi.entity.Institution;
import com.divudi.entity.Item;
import com.divudi.entity.pharmacy.PharmaceuticalBillItem;
import com.divudi.entity.pharmacy.Stock;
import com.divudi.facade.BillFacade;
import com.divudi.facade.BillItemFacade;
import com.divudi.facade.ItemFacade;
import com.divudi.facade.ItemsDistributorsFacade;
import com.divudi.facade.PharmaceuticalBillItemFacade;
import com.divudi.facade.StockFacade;
import com.divudi.facade.util.JsfUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author safrin
 */
@Named
@SessionScoped
public class TransferRequestController implements Serializable {

    @Inject
    private SessionController sessionController;
    @Inject
    CommonController commonController;
    @EJB
    private ItemFacade itemFacade;
    @EJB
    private BillNumberGenerator billNumberBean;
    @EJB
    private BillFacade billFacade;
    @EJB
    private BillItemFacade billItemFacade;
    @EJB
    private PharmaceuticalBillItemFacade pharmaceuticalBillItemFacade;
    @EJB
    private PharmacyBean pharmacyBean;
    @EJB
    private ItemsDistributorsFacade itemsDistributorsFacade;
    private Bill bill;
    private Institution dealor;
    private BillItem currentBillItem;
    private List<BillItem> billItems;
    @Inject
    private PharmacyCalculation pharmacyBillBean;
    private boolean printPreview;

    public void recreate() {
        Date startTime = new Date();
        Date fromDate = null;
        Date toDate = null;

        bill = null;
        currentBillItem = null;
        dealor = null;
        billItems = null;
        printPreview = false;
        
        commonController.printReportDetails(fromDate, toDate, startTime, "Theater/Transfer/request(New Bill)(/faces/theater/theater_transfer_request.xhtml)");
        
    }

    private boolean checkItems(Item item) {
        for (BillItem b : getBillItems()) {
            if (b.getItem().getId() == item.getId()) {
                return true;
            }
        }
        return false;
    }

    private boolean errorCheck() {
        if (getBill().getToDepartment() == null) {
            UtilityController.addErrorMessage("Select Department");
            return true;
        }

        if (getBill().getToDepartment().getId() == getSessionController().getDepartment().getId()) {
            UtilityController.addErrorMessage("U can't request same department");
            return true;
        }

        if (getCurrentBillItem().getItem() == null) {
            UtilityController.addErrorMessage("Select Item");
            return true;
        }

        if (getCurrentBillItem().getTmpQty() == 0) {
            UtilityController.addErrorMessage("Set Ordering Qty");
            return true;
        }

        if (checkItems(getCurrentBillItem().getItem())) {
            UtilityController.addErrorMessage("Item is Already Added");
            return true;
        }
        
        if (getBillItems().size()>=10) {
            UtilityController.addErrorMessage("You Can Only Add 10 Items For this Request.");
            return true;
        }

        return false;

    }

    @EJB
    StockFacade stockFacade;

    public void addAllItem() {

        if (getBill().getToDepartment() == null) {
            JsfUtil.addErrorMessage("Dept ?");
            return;
        }
        String jpql = "select s from Stock s where s.department=:dept";
        Map m = new HashMap();
        m.put("dept", getBill().getToDepartment());
        List<Stock> allAvailableStocks = stockFacade.findBySQL(jpql, m);
        for (Stock s : allAvailableStocks) {
            currentBillItem = null;
            getCurrentBillItem().setItem(s.getItemBatch().getItem());
            getCurrentBillItem().setTmpQty(s.getStock());
            addItem();
        }
        if (errorCheck()) {
            currentBillItem = null;
            return;
        }

        getCurrentBillItem().setSearialNo(getBillItems().size());

        getCurrentBillItem().getPharmaceuticalBillItem().setPurchaseRateInUnit(getPharmacyBean().getLastPurchaseRate(getCurrentBillItem().getItem(), getSessionController().getDepartment()));
        getCurrentBillItem().getPharmaceuticalBillItem().setRetailRateInUnit(getPharmacyBean().getLastRetailRate(getCurrentBillItem().getItem(), getSessionController().getDepartment()));

        getBillItems().add(getCurrentBillItem());

        currentBillItem = null;
    }

    public void addItem() {

        if (errorCheck()) {
            currentBillItem = null;
            return;
        }

        getCurrentBillItem().setSearialNo(getBillItems().size());

        getCurrentBillItem().getPharmaceuticalBillItem().setPurchaseRateInUnit(getPharmacyBean().getLastPurchaseRate(getCurrentBillItem().getItem(), getSessionController().getDepartment()));
        getCurrentBillItem().getPharmaceuticalBillItem().setRetailRateInUnit(getPharmacyBean().getLastRetailRate(getCurrentBillItem().getItem(), getSessionController().getDepartment()));

        getBillItems().add(getCurrentBillItem());

        currentBillItem = null;
    }
    @Inject
    private PharmacyController pharmacyController;

    public void onEdit(BillItem tmp) {
        getPharmacyController().setPharmacyItem(tmp.getItem());
    }

    public void onEdit() {
        getPharmacyController().setPharmacyItem(getCurrentBillItem().getItem());
    }

    public void saveBill() {
        if (getBill().getId() == null) {

            getBill().setInstitution(getSessionController().getInstitution());
            getBill().setDepartment(getSessionController().getDepartment());

            getBill().setToInstitution(getBill().getToDepartment().getInstitution());

            getBillFacade().create(getBill());
        }

    }

    public void request() {
        Date startTime = new Date();
        Date fromDate = null;
        Date toDate = null;

        if (getBill().getToDepartment() == null) {
            UtilityController.addErrorMessage("Select Requested Department");
            return;
        }

        if (getBill().getToDepartment() == getSessionController().getDepartment()) {
            UtilityController.addErrorMessage("U cant request ur department itself");
            return;
        }

        for (BillItem bi : getBillItems()) {
            if (bi.getQty() == 0.0) {
                UtilityController.addErrorMessage("Check Items Qty");
                return;
            }
        }

        saveBill();

        getBill().setDeptId(getBillNumberBean().institutionBillNumberGenerator(getSessionController().getDepartment(), BillType.PharmacyTransferRequest, BillClassType.BilledBill, BillNumberSuffix.PHTRQ));
        getBill().setInsId(getBillNumberBean().institutionBillNumberGenerator(getSessionController().getInstitution(), BillType.PharmacyTransferRequest, BillClassType.BilledBill, BillNumberSuffix.PHTRQ));

        getBill().setCreater(getSessionController().getLoggedUser());
        getBill().setCreatedAt(Calendar.getInstance().getTime());

        getBillFacade().edit(getBill());

        for (BillItem b : getBillItems()) {
            b.setBill(getBill());
            b.setCreatedAt(new Date());
            b.setCreater(getSessionController().getLoggedUser());

            PharmaceuticalBillItem tmpPh = b.getPharmaceuticalBillItem();
            b.setPharmaceuticalBillItem(null);

            if (b.getId() == null) {
                getBillItemFacade().create(b);
            }

            if (tmpPh.getId() == null) {
                getPharmaceuticalBillItemFacade().create(tmpPh);
            }

            b.setPharmaceuticalBillItem(tmpPh);
            getPharmaceuticalBillItemFacade().edit(tmpPh);
            getBillItemFacade().edit(b);

            getBill().getBillItems().add(b);
        }

        getBillFacade().edit(getBill());

        UtilityController.addSuccessMessage("Transfer Request Succesfully Created");

        printPreview = true;

        commonController.printReportDetails(fromDate, toDate, startTime, "Theater/Transfer/request(/faces/theater/theater_transfer_request.xhtml)");

    }

    public void remove(BillItem billItem) {
        getBillItems().remove(billItem.getSearialNo());
        int serialNo = 0;
        for (BillItem bi : getBillItems()) {
            bi.setSearialNo(serialNo++);
        }

    }

    public TransferRequestController() {
    }

    public Institution getDealor() {

        return dealor;
    }

    public void setDealor(Institution dealor) {
        this.dealor = dealor;
    }

    public BillNumberGenerator getBillNumberBean() {
        return billNumberBean;
    }

    public void setBillNumberBean(BillNumberGenerator billNumberBean) {
        this.billNumberBean = billNumberBean;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public BillFacade getBillFacade() {
        return billFacade;
    }

    public void setBillFacade(BillFacade billFacade) {
        this.billFacade = billFacade;
    }

    public ItemFacade getItemFacade() {
        return itemFacade;
    }

    public void setItemFacade(ItemFacade itemFacade) {
        this.itemFacade = itemFacade;
    }

    public Bill getBill() {
        if (bill == null) {
            bill = new BilledBill();
            bill.setBillType(BillType.PharmacyTransferRequest);
        }
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public BillItemFacade getBillItemFacade() {
        return billItemFacade;
    }

    public void setBillItemFacade(BillItemFacade billItemFacade) {
        this.billItemFacade = billItemFacade;
    }

    public PharmaceuticalBillItemFacade getPharmaceuticalBillItemFacade() {
        return pharmaceuticalBillItemFacade;
    }

    public void setPharmaceuticalBillItemFacade(PharmaceuticalBillItemFacade pharmaceuticalBillItemFacade) {
        this.pharmaceuticalBillItemFacade = pharmaceuticalBillItemFacade;
    }

    public PharmacyBean getPharmacyBean() {
        return pharmacyBean;
    }

    public void setPharmacyBean(PharmacyBean pharmacyBean) {
        this.pharmacyBean = pharmacyBean;
    }

    public ItemsDistributorsFacade getItemsDistributorsFacade() {
        return itemsDistributorsFacade;
    }

    public void setItemsDistributorsFacade(ItemsDistributorsFacade itemsDistributorsFacade) {
        this.itemsDistributorsFacade = itemsDistributorsFacade;
    }

    public PharmacyCalculation getPharmacyBillBean() {
        return pharmacyBillBean;
    }

    public void setPharmacyBillBean(PharmacyCalculation pharmacyBillBean) {
        this.pharmacyBillBean = pharmacyBillBean;
    }

    public PharmacyController getPharmacyController() {
        return pharmacyController;
    }

    public void setPharmacyController(PharmacyController pharmacyController) {
        this.pharmacyController = pharmacyController;
    }

//    public boolean isPrintPreview() {
//        return printPreview;
//    }
//
//    public void setPrintPreview(boolean printPreview) {
//        this.printPreview = printPreview;
//    }
    public BillItem getCurrentBillItem() {
        if (currentBillItem == null) {
            currentBillItem = new BillItem();
            PharmaceuticalBillItem ph = new PharmaceuticalBillItem();
            ph.setBillItem(currentBillItem);
            currentBillItem.setPharmaceuticalBillItem(ph);
        }
        return currentBillItem;
    }

    public void setCurrentBillItem(BillItem currentBillItem) {

        this.currentBillItem = currentBillItem;
        if (currentBillItem != null && currentBillItem.getItem() != null) {
            getPharmacyController().setPharmacyItem(currentBillItem.getItem());
        }
    }

    public List<BillItem> getBillItems() {
        if (billItems == null) {
            billItems = new ArrayList<>();
        }
        return billItems;
    }

    public void setBillItems(List<BillItem> billItems) {
        this.billItems = billItems;
    }

    public boolean isPrintPreview() {
        return printPreview;
    }

    public void setPrintPreview(boolean printPreview) {
        this.printPreview = printPreview;
    }

    public CommonController getCommonController() {
        return commonController;
    }

    public void setCommonController(CommonController commonController) {
        this.commonController = commonController;
    }

}
