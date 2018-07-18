/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.ws.channel;

import com.divudi.bean.common.CommonController;
import com.divudi.bean.common.InstitutionController;
import com.divudi.data.HistoryType;
import com.divudi.data.InstitutionType;
import com.divudi.ejb.CommonFunctions;
import com.divudi.entity.AgentHistory;
import com.divudi.entity.Institution;
import com.divudi.facade.AgentHistoryFacade;
import com.divudi.facade.InstitutionFacade;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.TemporalType;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author Archmage-Dushan
 */
@Path("apiChannelReport")
@RequestScoped
public class ApiChannelReport {

    @EJB
    private AgentHistoryFacade agentHistoryFacade;
    @EJB
    private InstitutionFacade institutionFacade;

    @Inject
    private InstitutionController institutionController;
    @Inject
    private CommonController commonController;
    @Inject
    private CommonFunctions commonFunctions;

    @Context
    private UriInfo context;

    public ApiChannelReport() {
    }

    //--Api
    @GET
    @Path("/duplicates/")
    @Produces("application/json")
    public String getDuplicateChannels() {
        System.err.println("~~~~~~Channel Report API~~~~~~ Get Duplicats(/duplicates)");
        JSONArray array = new JSONArray();
        JSONObject jSONObjectOut = new JSONObject();
        try {
            List<AgentHistory> agentHistorys = fetchAgentHistory(null, null, null);
            if (!agentHistorys.isEmpty()) {
                AgentHistory lastHistory = null;
                for (AgentHistory a : agentHistorys) {
                    if (lastHistory == null) {
                        lastHistory = a;
                        continue;
                    }

                    if (lastHistory.getReferenceNo() != null && lastHistory.getReferenceNo().equals(a.getReferenceNo())) {
                        JSONObject object = new JSONObject();
                        object.put("history_id", lastHistory.getId());
                        object.put("history_bill_id", lastHistory.getBill().getInsId());
                        object.put("history_ref_no", lastHistory.getReferenceNo());
                        object.put("history_created_at", getCommonController().getDateTimeFormat12(lastHistory.getBill().getCreatedAt()));
                        object.put("history_session_date", getCommonController().getDateFormat(lastHistory.getBill().getSingleBillSession().getSessionDate()));
                        object.put("history_session_time", getCommonController().getTimeFormat12(lastHistory.getBill().getSingleBillSession().getServiceSession().getStartingTime()));
                        object.put("history_doc_fee", lastHistory.getBill().getStaffFee());
                        object.put("history_total_fee", lastHistory.getBill().getVatPlusNetTotal());
                        object.put("history_before_value", lastHistory.getBeforeBallance());
                        object.put("history_transaction_value", lastHistory.getTransactionValue());
                        array.put(object);
                        
                        object = new JSONObject();
                        object.put("history_id", a.getId());
                        object.put("history_bill_id", a.getBill().getInsId());
                        object.put("history_ref_no", a.getReferenceNo());
                        object.put("history_created_at", getCommonController().getDateTimeFormat12(a.getBill().getCreatedAt()));
                        object.put("history_session_date", getCommonController().getDateFormat(a.getBill().getSingleBillSession().getSessionDate()));
                        object.put("history_session_time", getCommonController().getTimeFormat12(a.getBill().getSingleBillSession().getServiceSession().getStartingTime()));
                        object.put("history_doc_fee", a.getBill().getStaffFee());
                        object.put("history_total_fee", a.getBill().getVatPlusNetTotal());
                        object.put("history_before_value", a.getBeforeBallance());
                        object.put("history_transaction_value", a.getTransactionValue());
                        array.put(object);
                    }
                    lastHistory=a;
                }
                jSONObjectOut.put("history", array);
                jSONObjectOut.put("error", "0");
                jSONObjectOut.put("error_description", "");
            } else {
                jSONObjectOut.put("history", "");
                jSONObjectOut.put("error", "1");
                jSONObjectOut.put("error_description", "No Data.");
            }
        } catch (Exception e) {
            jSONObjectOut.put("history", "");
            jSONObjectOut.put("error", "1");
            jSONObjectOut.put("error_description", "Invalid Argument.");
        }
        String json = jSONObjectOut.toString();
        System.err.println("~~~~~~Channel Report API~~~~~~ Get Duplicats(/duplicats) = " + json);
        return json;
    }
    
    @GET
    @Path("/duplicates/{from_date}/{to_date}")
    @Produces("application/json")
    public String getDuplicatChannels(@PathParam("from_date") String from_date, @PathParam("to_date") String to_date) {
        System.err.println("~~~~~~Channel Report API~~~~~~ Get Duplicats(/duplicates)");
        JSONArray array = new JSONArray();
        JSONObject jSONObjectOut = new JSONObject();
        try {
            Date fromDate = getCommonController().getConvertDateTimeFormat24(from_date);
            Date toDate = getCommonController().getConvertDateTimeFormat24(to_date);
            List<AgentHistory> agentHistorys = fetchAgentHistory(null, fromDate, toDate);
            if (!agentHistorys.isEmpty()) {
                AgentHistory lastHistory = null;
                for (AgentHistory a : agentHistorys) {
                    if (lastHistory == null) {
                        lastHistory = a;
                        continue;
                    }

                    if (lastHistory.getReferenceNo() != null && lastHistory.getReferenceNo().equals(a.getReferenceNo())) {
                        JSONObject object = new JSONObject();
                        object.put("history_id", lastHistory.getId());
                        object.put("history_bill_id", lastHistory.getBill().getInsId());
                        object.put("history_ref_no", lastHistory.getReferenceNo());
                        object.put("history_created_at", getCommonController().getDateTimeFormat12(lastHistory.getBill().getCreatedAt()));
                        object.put("history_session_date", getCommonController().getDateFormat(lastHistory.getBill().getSingleBillSession().getSessionDate()));
                        object.put("history_session_time", getCommonController().getTimeFormat12(lastHistory.getBill().getSingleBillSession().getServiceSession().getStartingTime()));
                        object.put("history_doc_fee", lastHistory.getBill().getStaffFee());
                        object.put("history_total_fee", lastHistory.getBill().getVatPlusNetTotal());
                        object.put("history_before_value", lastHistory.getBeforeBallance());
                        object.put("history_transaction_value", lastHistory.getTransactionValue());
                        array.put(object);
                        
                        object = new JSONObject();
                        object.put("history_id", a.getId());
                        object.put("history_bill_id", a.getBill().getInsId());
                        object.put("history_ref_no", a.getReferenceNo());
                        object.put("history_created_at", getCommonController().getDateTimeFormat12(a.getBill().getCreatedAt()));
                        object.put("history_session_date", getCommonController().getDateFormat(a.getBill().getSingleBillSession().getSessionDate()));
                        object.put("history_session_time", getCommonController().getTimeFormat12(a.getBill().getSingleBillSession().getServiceSession().getStartingTime()));
                        object.put("history_doc_fee", a.getBill().getStaffFee());
                        object.put("history_total_fee", a.getBill().getVatPlusNetTotal());
                        object.put("history_before_value", a.getBeforeBallance());
                        object.put("history_transaction_value", a.getTransactionValue());
                        array.put(object);
                    }
                    lastHistory=a;
                }
                jSONObjectOut.put("history", array);
                jSONObjectOut.put("error", "0");
                jSONObjectOut.put("error_description", "");
            } else {
                jSONObjectOut.put("history", "");
                jSONObjectOut.put("error", "1");
                jSONObjectOut.put("error_description", "No Data.");
            }
        } catch (Exception e) {
            jSONObjectOut.put("history", "");
            jSONObjectOut.put("error", "1");
            jSONObjectOut.put("error_description", "Invalid Argument.");
        }
        String json = jSONObjectOut.toString();
        System.err.println("~~~~~~Channel Report API~~~~~~ Get Duplicats(/duplicates) = " + json);
        return json;
    }

    @GET
    @Path("/agencies/")
    @Produces("application/json")
    public String getAgencies() {
        System.err.println("~~~~~~Channel Report API~~~~~~ Get Agencies(/agencies)");
        JSONArray array = new JSONArray();
        JSONObject jSONObjectOut = new JSONObject();
        try {
            List<Institution> agencies = fetchAgencies();
            if (!agencies.isEmpty()) {
                for (Institution i : agencies) {
                    JSONObject object = new JSONObject();
                    object.put("agency_id", i.getId());
                    object.put("agency_name", i.getName());
                    object.put("agency_code", i.getInstitutionCode());
                    array.put(object);
                }
                jSONObjectOut.put("agency", array);
                jSONObjectOut.put("error", "0");
                jSONObjectOut.put("error_description", "");
            } else {
                jSONObjectOut.put("agency", "");
                jSONObjectOut.put("error", "1");
                jSONObjectOut.put("error_description", "No Data.");
            }
        } catch (Exception e) {
            jSONObjectOut.put("agency", "");
            jSONObjectOut.put("error", "1");
            jSONObjectOut.put("error_description", "Invalid Argument.");
        }
        String json = jSONObjectOut.toString();
        System.err.println("~~~~~~Channel Report API~~~~~~ Get Agencies(/agencies) = " + json);
        return json;

    }

    //--Methords
    private List<Institution> fetchAgencies() {
        List<Institution> agencies = getInstitutionController().completeInstitution(null, InstitutionType.Agency);
        return agencies;
    }

    private List<AgentHistory> fetchAgentHistory(Institution agency, Date fd, Date td) {
        if (agency == null) {
            agency = getInstitutionFacade().find(20385287l);
        }

        return createAgentHistory(fd, td, agency, Arrays.asList(new HistoryType[]{HistoryType.ChannelBooking,
            HistoryType.ChannelDeposit, HistoryType.ChannelDepositCancel, HistoryType.ChannelDebitNote,
            HistoryType.ChannelDebitNoteCancel, HistoryType.ChannelCreditNote, HistoryType.ChannelCreditNoteCancel}));
    }

    public List<AgentHistory> createAgentHistory(Date fd, Date td, Institution i, List<HistoryType> hts) {
        String sql;
        Map m = new HashMap();

        sql = " select ah from AgentHistory ah where ah.retired=false "
                + " and ah.bill.retired=false "
                + " and ah.createdAt between :fd and :td ";

        if (i != null) {
            sql += " and (ah.bill.fromInstitution=:ins"
                    + " or ah.bill.creditCompany=:ins) ";

            m.put("ins", i);
        }

        if (hts != null) {
            sql += " and ah.historyType in :hts ";

            m.put("hts", hts);
        }
        if (fd == null || td == null) {
            m.put("fd", getCommonFunctions().getStartOfDay());
            m.put("td", getCommonFunctions().getEndOfDay());
        } else {
            m.put("fd", getCommonFunctions().getStartOfDay(fd));
            m.put("td", getCommonFunctions().getEndOfDay(td));
        }

        sql += " order by ah.createdAt ";

        List<AgentHistory> agentHistorys = getAgentHistoryFacade().findBySQL(sql, m, TemporalType.TIMESTAMP);

//        System.out.println("m = " + m);
//        System.out.println("sql = " + sql);
//        System.out.println("agentHistorys.size() = " + agentHistorys.size());

        return agentHistorys;

    }

    //--Getters and Setters
    public UriInfo getContext() {
        return context;
    }

    public void setContext(UriInfo context) {
        this.context = context;
    }

    public InstitutionController getInstitutionController() {
        return institutionController;
    }

    public void setInstitutionController(InstitutionController institutionController) {
        this.institutionController = institutionController;
    }

    public CommonController getCommonController() {
        return commonController;
    }

    public void setCommonController(CommonController commonController) {
        this.commonController = commonController;
    }

    public AgentHistoryFacade getAgentHistoryFacade() {
        return agentHistoryFacade;
    }

    public void setAgentHistoryFacade(AgentHistoryFacade agentHistoryFacade) {
        this.agentHistoryFacade = agentHistoryFacade;
    }

    public CommonFunctions getCommonFunctions() {
        return commonFunctions;
    }

    public void setCommonFunctions(CommonFunctions commonFunctions) {
        this.commonFunctions = commonFunctions;
    }

    public InstitutionFacade getInstitutionFacade() {
        return institutionFacade;
    }

    public void setInstitutionFacade(InstitutionFacade institutionFacade) {
        this.institutionFacade = institutionFacade;
    }

}
