/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.bean.hr;

import com.divudi.bean.common.CommonController;
import com.divudi.bean.common.SessionController;
import com.divudi.bean.common.UtilityController;
import com.divudi.bean.common.util.JsfUtil;
import com.divudi.data.dataStructure.ShiftTable;
import com.divudi.data.hr.DayType;
import com.divudi.ejb.CommonFunctions;
import com.divudi.ejb.HumanResourceBean;
import com.divudi.entity.Staff;
import com.divudi.entity.hr.Roster;
import com.divudi.entity.hr.Shift;
import com.divudi.entity.hr.StaffShift;
import com.divudi.entity.hr.StaffShiftExtra;
import com.divudi.entity.hr.StaffShiftHistory;
import com.divudi.facade.StaffFacade;
import com.divudi.facade.StaffShiftFacade;
import com.divudi.facade.StaffShiftHistoryFacade;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.TemporalType;

/**
 *
 * @author safrin
 */
@Named
@SessionScoped
public class ShiftTableController implements Serializable {

    Date fromDate;
    Date toDate;
    Long dateRange;
    Roster roster;
    Shift shift;
    StaffShift staffShift;
    List<ShiftTable> shiftTables;
    @EJB
    HumanResourceBean humanResourceBean;
    @EJB
    CommonFunctions commonFunctions;
    @EJB
    StaffShiftFacade staffShiftFacade;
    @Inject
    SessionController sessionController;
    @Inject
    ShiftController shiftController;
    @Inject
    StaffShiftController staffShiftController;
    @Inject
    CommonController commonController;
    boolean all;
    Staff staff;

    //FUNTIONS
    public void makeNull() {
        fromDate = null;
        toDate = null;
        dateRange = 0l;
        roster = null;
        shiftTables = null;

    }

    private boolean errorCheck() {
        if (getFromDate() == null || getToDate() == null) {
            return true;
        }

        return false;
    }
    
    private boolean errorCheckAutoRoster() {
        if (getFromDate() == null || getToDate() == null) {
            JsfUtil.addErrorMessage("Please Select From Date &amp; To Date");
            return true;
        }
        if (checkRosterAlradyExsist(getFromDate(), getToDate(), getRoster())) {
            JsfUtil.addErrorMessage("This date range Alrady Filled");
            return true;
        }
        if (checkSelectedDateRange(getFromDate(), getToDate())) {
            JsfUtil.addErrorMessage("Please Selected Date range Should be start Sunday &amp; end with Saturday and Only Seven Days allowed");
            return true;
        }

        return false;
    }
    
    public boolean checkRosterAlradyExsist(Date fd,Date td,Roster ros) {
        Date startTime = new Date();

        if (errorCheck()) {
            return true;
        }

        shiftTables = new ArrayList<>();

        Calendar nc = Calendar.getInstance();
        nc.setTime(getFromDate());
        Date nowDate = nc.getTime();

        nc.setTime(getToDate());
        nc.add(Calendar.DATE, 1);
        Date tmpToDate = nc.getTime();

        //CREATE FIRTS TABLE For Indexing Purpuse
        ShiftTable netT;

        while (tmpToDate.after(nowDate)) {
            netT = new ShiftTable();
            netT.setDate(nowDate);

            Calendar calNowDate = Calendar.getInstance();
            calNowDate.setTime(nowDate);

            Calendar calFromDate = Calendar.getInstance();
            calFromDate.setTime(getFromDate());

            if (calNowDate.get(Calendar.DATE) == calFromDate.get(Calendar.DATE)) {
                netT.setFlag(Boolean.TRUE);
            } else {
                netT.setFlag(Boolean.FALSE);
            }

            List<Staff> staffs = getHumanResourceBean().fetchStaffShift(fd, td, ros);
            
            for (Staff staff : staffs) {
                System.out.println("staff = " + staff.getPerson().getName());
                List<StaffShift> ss = getHumanResourceBean().fetchStaffShift(nowDate, staff);
                System.out.println("ss.size() = " + ss.size());
                if (ss.size()>0) {
                    return true;
                } 
            }


            Calendar c = Calendar.getInstance();
            c.setTime(nowDate);
            c.add(Calendar.DATE, 1);
            nowDate = c.getTime();

        }

        return false;
    }
    
    public boolean checkSelectedDateRange(Date fd,Date td){
        
        long coount=commonFunctions.getDayCount(fd, td);
        System.out.println("coount = " + coount);
        if (coount!=7) {
            JsfUtil.addErrorMessage("Date Range Should Be 7 Days");
            return true;
        }
        Calendar cal=Calendar.getInstance();
        cal.setTime(fd);
        System.out.println("cal = " + cal.get(Calendar.DAY_OF_WEEK));
        if (cal.get(Calendar.DAY_OF_WEEK)!=1) {
            JsfUtil.addErrorMessage("From Date Shoulde be Start from Sunday");
            return true;
        }
        cal.setTime(fd);
        System.out.println("cal = " + cal.get(Calendar.DAY_OF_WEEK));
        if (cal.get(Calendar.DAY_OF_WEEK)!=7) {
            JsfUtil.addErrorMessage("To Date Shoulde be End with Saturdaty");
            return true;
        }
        return false;
    }

    @Inject
    PhDateController phDateController;

    public void fetchAndSetDayType(StaffShift ss) {
        DayType dayType = null;
        if (ss.getShift() != null) {
            dayType = ss.getShift().getDayType();
        }

        System.out.println("ss.getDayType() = " + ss.getDayType());

        ss.setDayType(null);

        DayType dtp;
        if (dayType != null || dayType == DayType.DayOff) {
            dtp = dayType;
        } else {
            dtp = phDateController.getHolidayType(ss.getShiftDate());
        }

        ss.setDayType(dtp);
        if (ss.getDayType() == null) {
            if (ss.getShift() != null) {
                ss.setDayType(ss.getShift().getDayType());
            }
        }
    }

    private void saveStaffShift() {
        for (ShiftTable st : shiftTables) {
            for (StaffShift ss : st.getStaffShift()) {
                if (ss.getId()==null && ss.getShift() == null) {
                    continue;
                }

                fetchAndSetDayType(ss);
                ss.calShiftStartEndTime();
                ss.calLieu();
                if (ss.getId() == null) {
                    getStaffShiftFacade().create(ss);
                }

                ss.setPreviousStaffShift(getHumanResourceBean().calPrevStaffShift(ss));
                ss.setNextStaffShift(getHumanResourceBean().calFrwStaffShift(ss));
                getStaffShiftFacade().edit(ss);
            }
        }
    }

    @EJB
    StaffShiftHistoryFacade staffShiftHistoryFacade;

    private void saveHistory() {
        for (ShiftTable st : shiftTables) {
            for (StaffShift ss : st.getStaffShift()) {

                if (ss.getId() != null) {
                    boolean flag = false;
                    StaffShift fetchStaffShift = staffShiftFacade.find(ss.getId());
                    if (fetchStaffShift.getRoster() != null && ss.getRoster() != null) {
                        if (!fetchStaffShift.getRoster().equals(ss.getRoster())) {
                            flag = true;
                        }
                    }

                    if (fetchStaffShift.getStaff() != null && ss.getStaff() != null) {
                        if (!fetchStaffShift.getStaff().equals(ss.getStaff())) {
                            flag = true;
                        }
                    }
                    if (fetchStaffShift.getShift() != null && ss.getShift() != null) {
                        if (!fetchStaffShift.getShift().equals(ss.getShift())) {
                            System.out.println("Shift true");
                            System.out.println("fetchStaffShift.fetchStaffShift.getShift().getId() = " + fetchStaffShift.getShift().getId());
                            flag = true;
                        }
                    }

                    if (flag) {
                        StaffShiftHistory staffShiftHistory = new StaffShiftHistory();
                        staffShiftHistory.setStaffShift(ss);
                        staffShiftHistory.setCreatedAt(new Date());
                        staffShiftHistory.setCreater(sessionController.getLoggedUser());
                        //CHanges
                        staffShiftHistory.setStaff(ss.getStaff());
                        staffShiftHistory.setShift(ss.getShift());
                        staffShiftHistory.setRoster(ss.getRoster());

                        staffShiftHistoryFacade.create(staffShiftHistory);
                    }
                }

            }
        }
    }

    public void save() {
        if (shiftTables == null) {
            return;
        }

        saveHistory();

        saveStaffShift();
        saveStaffShift();

    }

    public void createShiftTable() {
        Date startTime = new Date();

        if (errorCheck()) {
            return;
        }

        shiftTables = new ArrayList<>();

        Calendar nc = Calendar.getInstance();
        nc.setTime(getFromDate());
        Date nowDate = nc.getTime();

        nc.setTime(getToDate());
        nc.add(Calendar.DATE, 1);
        Date tmpToDate = nc.getTime();

        //CREATE FIRTS TABLE For Indexing Purpuse
        ShiftTable netT;

        while (tmpToDate.after(nowDate)) {
            netT = new ShiftTable();
            netT.setDate(nowDate);

            Calendar calNowDate = Calendar.getInstance();
            calNowDate.setTime(nowDate);

            Calendar calFromDate = Calendar.getInstance();
            calFromDate.setTime(getFromDate());

            if (calNowDate.get(Calendar.DATE) == calFromDate.get(Calendar.DATE)) {
                netT.setFlag(Boolean.TRUE);
            } else {
                netT.setFlag(Boolean.FALSE);
            }

            for (Staff stf : getHumanResourceBean().fetchStaff(getRoster())) {
                List<StaffShift> staffShifts = getHumanResourceBean().fetchStaffShift(nowDate, stf);
                System.out.println("staffShifts.size() = " + staffShifts.size());
                if (staffShifts.isEmpty()) {
                    for (int i = getRoster().getShiftPerDay(); i > 0; i--) {
                        StaffShift ss = new StaffShift();
                        ss.setStaff(stf);
                        ss.setShiftDate(nowDate);
                        ss.setRoster(roster);
                        netT.getStaffShift().add(ss);
                    }
                } else {
                    for (StaffShift ss : staffShifts) {
                        System.out.println("ss.getRoster() = " + ss.getRoster());
                        netT.getStaffShift().add(ss);
                    }
                }

            }
            shiftTables.add(netT);

            Calendar c = Calendar.getInstance();
            c.setTime(nowDate);
            c.add(Calendar.DATE, 1);
            nowDate = c.getTime();

        }

        Long range = getCommonFunctions().getDayCount(getFromDate(), getToDate());
        setDateRange(range + 1);

        commonController.printReportDetails(fromDate, toDate, startTime, "HR/Working Time/Roster table(Fill New)(/faces/hr/hr_shift_table.xhtml)");
    }

    public void fetchShiftTable() {
        Date startTime = new Date();

        if (errorCheck()) {
            return;
        }

        shiftTables = new ArrayList<>();

        Calendar nc = Calendar.getInstance();
        nc.setTime(getFromDate());
        Date nowDate = nc.getTime();

        nc.setTime(getToDate());
        nc.add(Calendar.DATE, 1);
        Date tmpToDate = nc.getTime();

        //CREATE FIRTS TABLE For Indexing Purpuse
        ShiftTable netT;

        while (tmpToDate.after(nowDate)) {
            netT = new ShiftTable();
            netT.setDate(nowDate);

            Calendar calNowDate = Calendar.getInstance();
            calNowDate.setTime(nowDate);

            Calendar calFromDate = Calendar.getInstance();
            calFromDate.setTime(getFromDate());

            if (calNowDate.get(Calendar.DATE) == calFromDate.get(Calendar.DATE)) {
                netT.setFlag(Boolean.TRUE);
            } else {
                netT.setFlag(Boolean.FALSE);
            }

//            List<StaffShift> staffShifts = getHumanResourceBean().fetchStaffShift(nowDate, roster);
//
//            for (StaffShift ss : staffShifts) {
//                netT.getStaffShift().add(ss);
//            }
            List<Staff> staffs = getHumanResourceBean().fetchStaffShift(fromDate, toDate, roster);

            for (Staff staff : staffs) {
                List<StaffShift> ss = getHumanResourceBean().fetchStaffShift(nowDate, staff);
                if (ss == null) {
                    for (int i = 0; i < roster.getShiftPerDay(); i++) {
                        StaffShift newStaffShift = new StaffShift();
                        newStaffShift.setStaff(staff);
                        newStaffShift.setShiftDate(nowDate);
                        newStaffShift.setCreatedAt(new Date());
                        newStaffShift.setCreater(sessionController.getLoggedUser());
                        newStaffShift.setRoster(roster);
                        netT.getStaffShift().add(newStaffShift);
                    }
                } else {
                    netT.getStaffShift().addAll(ss);
                    int ballance = roster.getShiftPerDay() - ss.size();
                    if (ballance < 0) {
                        continue;
                    }
                    for (int i = 0; i < ballance; i++) {
                        StaffShift newStaffShift = new StaffShift();
                        newStaffShift.setStaff(staff);
                        newStaffShift.setShiftDate(nowDate);
                        newStaffShift.setCreatedAt(new Date());
                        newStaffShift.setCreater(sessionController.getLoggedUser());
                        newStaffShift.setRoster(roster);
                        netT.getStaffShift().add(newStaffShift);
                    }

                }
            }

            shiftTables.add(netT);

            Calendar c = Calendar.getInstance();
            c.setTime(nowDate);
            c.add(Calendar.DATE, 1);
            nowDate = c.getTime();

        }

        Long range = getCommonFunctions().getDayCount(getFromDate(), getToDate());
        setDateRange(range + 1);

        commonController.printReportDetails(fromDate, toDate, startTime, "HR/Working Time/Roster table(Fill Old Roster)(/faces/hr/hr_shift_table.xhtml)");
    }
    
    public void fetchShiftTable(Date fd,Date td,Roster ros) {
        Date startTime = new Date();

        if (errorCheck()) {
            return;
        }

        shiftTables = new ArrayList<>();

        Calendar nc = Calendar.getInstance();
        nc.setTime(getFromDate());
        Date nowDate = nc.getTime();

        nc.setTime(getToDate());
        nc.add(Calendar.DATE, 1);
        Date tmpToDate = nc.getTime();

        //CREATE FIRTS TABLE For Indexing Purpuse
        ShiftTable netT;

        while (tmpToDate.after(nowDate)) {
            netT = new ShiftTable();
            netT.setDate(nowDate);

            Calendar calNowDate = Calendar.getInstance();
            calNowDate.setTime(nowDate);

            Calendar calFromDate = Calendar.getInstance();
            calFromDate.setTime(getFromDate());

            if (calNowDate.get(Calendar.DATE) == calFromDate.get(Calendar.DATE)) {
                netT.setFlag(Boolean.TRUE);
            } else {
                netT.setFlag(Boolean.FALSE);
            }

//            List<StaffShift> staffShifts = getHumanResourceBean().fetchStaffShift(nowDate, roster);
//
//            for (StaffShift ss : staffShifts) {
//                netT.getStaffShift().add(ss);
//            }
            List<Staff> staffs = getHumanResourceBean().fetchStaffShift(fd, td, ros);

            for (Staff staff : staffs) {
                List<StaffShift> ss = getHumanResourceBean().fetchStaffShift(nowDate, staff);
                if (ss == null) {
                    for (int i = 0; i < roster.getShiftPerDay(); i++) {
                        StaffShift newStaffShift = new StaffShift();
                        newStaffShift.setStaff(staff);
                        newStaffShift.setShiftDate(nowDate);
                        newStaffShift.setCreatedAt(new Date());
                        newStaffShift.setCreater(sessionController.getLoggedUser());
                        newStaffShift.setRoster(roster);
                        netT.getStaffShift().add(newStaffShift);
                    }
                } else {
                    netT.getStaffShift().addAll(ss);
                    int ballance = roster.getShiftPerDay() - ss.size();
                    if (ballance < 0) {
                        continue;
                    }
                    for (int i = 0; i < ballance; i++) {
                        StaffShift newStaffShift = new StaffShift();
                        newStaffShift.setStaff(staff);
                        newStaffShift.setShiftDate(nowDate);
                        newStaffShift.setCreatedAt(new Date());
                        newStaffShift.setCreater(sessionController.getLoggedUser());
                        newStaffShift.setRoster(roster);
                        netT.getStaffShift().add(newStaffShift);
                    }

                }
            }

            shiftTables.add(netT);

            Calendar c = Calendar.getInstance();
            c.setTime(nowDate);
            c.add(Calendar.DATE, 1);
            nowDate = c.getTime();

        }

        Long range = getCommonFunctions().getDayCount(getFromDate(), getToDate());
        setDateRange(range + 1);

        commonController.printReportDetails(fromDate, toDate, startTime, "HR/Working Time/Roster table(Fill Old Roster)(/faces/hr/hr_shift_table.xhtml)");
    }

    public void fetchShiftTableByStaff() {
        if (errorCheck()) {
            return;
        }
        if (staff == null) {
            UtilityController.addErrorMessage("Plaese Select Staff");
            return;
        }

        shiftTables = new ArrayList<>();

        Calendar nc = Calendar.getInstance();
        nc.setTime(getFromDate());
        Date nowDate = nc.getTime();

        nc.setTime(getToDate());
        nc.add(Calendar.DATE, 1);
        Date tmpToDate = nc.getTime();

        //CREATE FIRTS TABLE For Indexing Purpuse
        ShiftTable netT;

        while (tmpToDate.after(nowDate)) {
            netT = new ShiftTable();
            netT.setDate(nowDate);

            Calendar calNowDate = Calendar.getInstance();
            calNowDate.setTime(nowDate);

            Calendar calFromDate = Calendar.getInstance();
            calFromDate.setTime(getFromDate());

            if (calNowDate.get(Calendar.DATE) == calFromDate.get(Calendar.DATE)) {
                netT.setFlag(Boolean.TRUE);
            } else {
                netT.setFlag(Boolean.FALSE);
            }

//            List<StaffShift> staffShifts = getHumanResourceBean().fetchStaffShift(nowDate, roster);
//
//            for (StaffShift ss : staffShifts) {
//                netT.getStaffShift().add(ss);
//            }
            List<StaffShift> ss = getHumanResourceBean().fetchStaffShift(nowDate, staff);
            if (ss == null) {
                UtilityController.addErrorMessage("No Staff Shift");
                return;
            } else {
                netT.getStaffShift().addAll(ss);

            }

            shiftTables.add(netT);

            Calendar c = Calendar.getInstance();
            c.setTime(nowDate);
            c.add(Calendar.DATE, 1);
            nowDate = c.getTime();

        }

        Long range = getCommonFunctions().getDayCount(getFromDate(), getToDate());
        setDateRange(range + 1);
    }

    public void selectRosterLstener() {
        makeTableNull();
        getShiftController().setCurrentRoster(roster);

    }

    public void fetchShiftTableForCheck() {
        Date startTime = new Date();

        if (errorCheck()) {
            return;
        }

        shiftTables = new ArrayList<>();

        Calendar nc = Calendar.getInstance();
        nc.setTime(getFromDate());
        Date nowDate = nc.getTime();

        nc.setTime(getToDate());
        nc.add(Calendar.DATE, 1);
        Date tmpToDate = nc.getTime();

        //CREATE FIRTS TABLE For Indexing Purpuse
        ShiftTable netT;

        ShiftTable summeryTable = new ShiftTable();
        summeryTable.setFlag(false);
        boolean b = false;
        int a = 0;
        while (tmpToDate.after(nowDate)) {
            netT = new ShiftTable();
            netT.setDate(nowDate);

            DayType dt = humanResourceBean.isHolidayWithDayType(nowDate);
            System.out.println("dt = " + dt);

            if (dt == DayType.MurchantileHoliday) {
                netT.setMerch(true);
            } else {
                netT.setMerch(false);
            }

            if (dt == DayType.Poya) {
                netT.setPh(true);
            } else {
                netT.setPh(false);
            }

            Calendar calNowDate = Calendar.getInstance();
            calNowDate.setTime(nowDate);

            Calendar calFromDate = Calendar.getInstance();
            calFromDate.setTime(getFromDate());

            if (calNowDate.get(Calendar.DATE) == calFromDate.get(Calendar.DATE)) {
                netT.setFlag(Boolean.TRUE);
            } else {
                netT.setFlag(Boolean.FALSE);
            }

//            List<StaffShift> staffShifts = getHumanResourceBean().fetchStaffShift(nowDate, roster);
//
//            for (StaffShift ss : staffShifts) {
//                netT.getStaffShift().add(ss);
//            }
            List<Staff> staffs = getHumanResourceBean().fetchStaffShift(fromDate, toDate, roster);
            if (b) {
                a = 0;
            }
            for (Staff staff : staffs) {
                System.out.println("staff.getPerson().getName() = " + staff.getPerson().getName());
                List<StaffShift> ss = getHumanResourceBean().fetchStaffShift(nowDate, staff);
                if (ss == null) {
                    for (int i = 0; i < roster.getShiftPerDay(); i++) {
                        StaffShift newStaffShift = new StaffShift();
                        newStaffShift.setStaff(staff);
                        newStaffShift.setShiftDate(nowDate);
                        newStaffShift.setCreatedAt(new Date());
                        newStaffShift.setCreater(sessionController.getLoggedUser());
                        newStaffShift.setTransWorkTime(0.0);
                        if (b) {
                            System.out.println("staff.getPerson().getName() = " + staff.getPerson().getName());
                            System.out.println("summeryTable.getStaffShift().get(a).getTransWorkTime() = " + summeryTable.getStaffShift().get(a).getTransWorkTime());
                            System.out.println("summeryTable.getStaffShift().get(a).getTransShiftTime() = " + summeryTable.getStaffShift().get(a).getTransShiftTime());
                            System.out.println("a = " + a);
                            summeryTable.getStaffShift().get(a).setTransWorkTime(summeryTable.getStaffShift().get(a).getTransWorkTime() + 0);
                            summeryTable.getStaffShift().get(a).setTransShiftTime(summeryTable.getStaffShift().get(a).getTransShiftTime() + 0);
                            a++;
                        } else {
                            StaffShift sss = new StaffShift();
                            sss.setTransShiftTime(0);
                            sss.setTransWorkTime(0);
                            summeryTable.getStaffShift().add(sss);
                        }
                        netT.getStaffShift().add(newStaffShift);
                    }
                } else {
                    for (StaffShift s : ss) {
                        System.out.println("s.getShift().getName() = " + s.getShift().getName());
                        if (s.getShift().getDurationMin() > 0) {
                            System.out.println("s.getTransWorkTime() = " + s.getTransWorkTime());
                            s.setTransWorkTime(fetchWorkTime(staff, nowDate));
                            if (b) {
                                System.out.println("b = " + b);
                                System.out.println("staff.getPerson().getName() = " + staff.getPerson().getName());
                                System.out.println("summeryTable.getStaffShift().get(a).getTransWorkTime() = " + summeryTable.getStaffShift().get(a).getTransWorkTime());
                                System.out.println("summeryTable.getStaffShift().get(a).getTransShiftTime() = " + summeryTable.getStaffShift().get(a).getTransShiftTime());
                                System.out.println("a = " + a);
                                summeryTable.getStaffShift().get(a).setTransWorkTime(summeryTable.getStaffShift().get(a).getTransWorkTime() + s.getTransWorkTime());
                                summeryTable.getStaffShift().get(a).setTransShiftTime(summeryTable.getStaffShift().get(a).getTransShiftTime() + s.getShift().getDurationMin());
                                a++;
                            } else {
                                StaffShift sss = new StaffShift();
                                sss.setTransShiftTime(s.getShift().getDurationMin());
                                sss.setTransWorkTime(s.getTransWorkTime());
                                summeryTable.getStaffShift().add(sss);
                            }
                        } else {
                            if (b) {
                                System.out.println("b = " + b);
                                System.out.println("staff.getPerson().getName() = " + staff.getPerson().getName());
                                System.out.println("summeryTable.getStaffShift().get(a).getTransWorkTime() = " + summeryTable.getStaffShift().get(a).getTransWorkTime());
                                System.out.println("summeryTable.getStaffShift().get(a).getTransShiftTime() = " + summeryTable.getStaffShift().get(a).getTransShiftTime());
                                System.out.println("a = " + a);
                                summeryTable.getStaffShift().get(a).setTransWorkTime(summeryTable.getStaffShift().get(a).getTransWorkTime() + s.getTransWorkTime());
                                summeryTable.getStaffShift().get(a).setTransShiftTime(summeryTable.getStaffShift().get(a).getTransShiftTime() + s.getShift().getDurationMin());
                                a++;
                            } else {
                                StaffShift sss = new StaffShift();
                                sss.setTransShiftTime(s.getShift().getDurationMin());
                                sss.setTransWorkTime(s.getTransWorkTime());
                                summeryTable.getStaffShift().add(sss);
                            }
                        }
                    }
                    netT.getStaffShift().addAll(ss);
                    System.out.println("roster.getShiftPerDay() = " + roster.getShiftPerDay());
                    System.out.println("ss.size() = " + ss.size());
                    int ballance = roster.getShiftPerDay() - ss.size();
                    if (ballance <= 0) {
                        continue;
                    }
                    for (int i = 0; i < ballance; i++) {
                        StaffShift newStaffShift = new StaffShift();
                        newStaffShift.setStaff(staff);
                        newStaffShift.setShiftDate(nowDate);
                        newStaffShift.setCreatedAt(new Date());
                        newStaffShift.setCreater(sessionController.getLoggedUser());
                        if (b) {
                            System.out.println("b = " + b);
                            System.out.println("staff.getPerson().getName() = " + staff.getPerson().getName());
                            System.out.println("summeryTable.getStaffShift().get(a).getTransWorkTime() = " + summeryTable.getStaffShift().get(a).getTransWorkTime());
                            System.out.println("summeryTable.getStaffShift().get(a).getTransShiftTime() = " + summeryTable.getStaffShift().get(a).getTransShiftTime());
                            System.out.println("a = " + a);
                            summeryTable.getStaffShift().get(a).setTransWorkTime(summeryTable.getStaffShift().get(a).getTransWorkTime() + 0);
                            summeryTable.getStaffShift().get(a).setTransShiftTime(summeryTable.getStaffShift().get(a).getTransShiftTime() + 0);
                            a++;
                        } else {
                            StaffShift sss = new StaffShift();
                            sss.setTransShiftTime(0);
                            sss.setTransWorkTime(0);
                            summeryTable.getStaffShift().add(sss);
                        }
                        netT.getStaffShift().add(newStaffShift);
                    }

                }
            }

            shiftTables.add(netT);

            Calendar c = Calendar.getInstance();
            c.setTime(nowDate);
            c.add(Calendar.DATE, 1);
            nowDate = c.getTime();
            b = true;
        }

        //
//        List<Staff> staffs = getHumanResourceBean().fetchStaffShift(fromDate, toDate, roster);
//
//        for (Staff staff : staffs) {
//            System.out.println("staff.getPerson().getName() = " + staff.getPerson().getName());
//
//            double timeRoster = 0.0;
//            double timeWork = 0.0;
//            System.out.println("shiftTables = " + shiftTables);
//            for (ShiftTable st : shiftTables) {
//                System.out.println("st.getStaffShift() = " + st.getStaffShift());
//                List<StaffShift> ss = getHumanResourceBean().fetchStaffShift(st.getDate(), staff);
//                System.out.println("ss.size() = " + ss.size());
//                for (StaffShift s : ss) {
//                    if (s.getStaff() == staff) {
//                        System.out.println("s.getStaff() = " + s.getStaff().getPerson().getName());
//                        System.out.println("staff = " + staff.getPerson().getName());
//                        System.out.println("timeRoster = " + timeRoster);
//                        System.out.println("timeWork = " + timeWork);
//                        timeRoster += s.getShift().getDurationHour();
//                        timeWork += s.getTransWorkTime();
//                        System.out.println("timeRoster = " + timeRoster);
//                        System.out.println("timeWork = " + timeWork);
//                    }
//                }
//            }
//            System.out.println("Total timeRoster = " + timeRoster);
//            System.out.println("Total timeWork = " + timeWork);
//            StaffShift nss = new StaffShift();
//            nss.setTransWorkTime(timeWork);
//            nss.setTransShiftTime(timeRoster);
//            summeryTable.getStaffShift().add(nss);
//        }
        shiftTables.add(summeryTable);
        //

        Long range = getCommonFunctions().getDayCount(getFromDate(), getToDate());
        setDateRange(range + 1);

        commonController.printReportDetails(fromDate, toDate, startTime, "HR/Reports/Shift/Roster table and vertify time(/faces/hr/hr_report_shift_table.xhtml)");
    }
    
    public void createNextWeekRoster(){
        if (errorCheckAutoRoster()) {
            return;
        }
        Calendar cal=Calendar.getInstance();
        cal.setTime(getFromDate());
        cal.add(Calendar.DATE, -7);
        Date LastWeekFD=cal.getTime();
        System.out.println("LastWeekFD = " + LastWeekFD);
        
        cal.setTime(getToDate());
        cal.add(Calendar.DATE, -7);
        Date LastWeekTD=cal.getTime();
        System.out.println("LastWeekTD = " + LastWeekTD);
    }

    public double fetchWorkTime(Staff staff, Date date) {

        Object[] obj = fetchWorkedTimeByDateOnly(staff, date);

        System.err.println("list = " + obj);

        Double value = (Double) obj[0] != null ? (Double) obj[0] : 0;
        Double valueExtra = (Double) obj[1] != null ? (Double) obj[1] : 0;
        Double totalExtraDuty = (Double) obj[2] != null ? (Double) obj[2] : 0;
        StaffShift ss = (StaffShift) obj[3] != null ? (StaffShift) obj[3] : new StaffShift();
        Double leavedTimeValue = (Double) obj[4] != null ? (Double) obj[4] : 0;

        System.err.println("Staff " + staff.getCodeInterger() + " :Value : " + value);
        if (ss.getShift() != null && ss.getShift().getLeaveHourHalf() != 0 && leavedTimeValue > 0) {
            System.out.println("value = " + value);
            System.out.println("leavedTimeValue = " + leavedTimeValue);
            if ((ss.getShift().getDurationMin() * 60) < value) {
                value = ss.getShift().getDurationMin() * 60;
            }
        }

        return value;

    }

    private Object[] fetchWorkedTimeByDateOnly(Staff staff, Date date) {
        String sql = "";

        HashMap hm = new HashMap();
        sql = "select "
                + " sum(ss.workedWithinTimeFrameVarified+ss.leavedTime),"
                + " sum(ss.extraTimeFromStartRecordVarified+ss.extraTimeFromEndRecordVarified),"
                + " sum((ss.extraTimeFromStartRecordVarified+ss.extraTimeFromEndRecordVarified)*ss.multiplyingFactorOverTime*ss.overTimeValuePerSecond), "
                + " ss, "
                + " sum(ss.leavedTime)"
                + " from StaffShift ss "
                + " where ss.retired=false "
                + " and type(ss)!=:tp"
                + " and ss.staff=:stf"
                //                + " and ss.leavedTime=0 "
                + " and ss.dayType not in :dtp "
                //                + " and ((ss.startRecord.recordTimeStamp is not null "
                //                + " and ss.endRecord.recordTimeStamp is not null) "
                //                + " or (ss.leaveType is not null) ) "
                + " and ss.shiftDate=:date ";
        hm.put("date", date);
        hm.put("tp", StaffShiftExtra.class);
        hm.put("stf", staff);
        hm.put("dtp", Arrays.asList(new DayType[]{DayType.DayOff, DayType.MurchantileHoliday, DayType.SleepingDay, DayType.Poya}));

        if (staff != null) {
            sql += " and ss.staff=:stf ";
            hm.put("stf", staff);
        }

        sql += " order by ss.dayOfWeek,ss.staff.codeInterger ";
        return staffShiftFacade.findAggregate(sql, hm, TemporalType.TIMESTAMP);
    }

    @EJB
    StaffFacade staffFacade;

    public void fetchStaffShiftMoreThan() {
        String sql = "Select distinct(ss.staff) from StaffShift ss "
                + " where ss.retired=false "
                + " order by ss.staff.codeInterger ";

        List<Staff> staffs = staffFacade.findBySQL(sql);
        System.out.println("staffs = " + staffs.size());

//        sql = "Select ss from StaffShift ss "
//                + " where ss.retired=false "
//                + " and ss.shiftDate is not null";
//
//        StaffShift staffShift = staffShiftFacade.findFirstBySQL(sql);
        Calendar nc = Calendar.getInstance();
        nc.setTime(new Date());
        nc.set(2015, 00, 01, 00, 00, 00);
        Date nowDate = nc.getTime();

        nc.setTime(new Date());
        nc.add(Calendar.DATE, 1);
        Date tmpToDate = nc.getTime();

        System.out.println("nowDate = " + nowDate);
        System.out.println("tmpToDate = " + tmpToDate);
        int i = 0;
        System.out.println("i(start) = " + i);
        while (tmpToDate.after(nowDate)) {

            for (Staff s : staffs) {
                List<StaffShift> ss = humanResourceBean.fetchStaffShift(nowDate, s);
                if (ss.size() > 2) {
                    System.err.println("ss.size() = " + ss.size());
                    System.err.println("nowDate = " + nowDate);
                    i++;
                    for (StaffShift sss : ss) {
                    }
                }
            }

            Calendar c = Calendar.getInstance();
            c.setTime(nowDate);
            c.add(Calendar.DATE, 1);
            nowDate = c.getTime();
        }
    }

    public void makeTableNull() {
        shiftTables = null;
    }

    //GETTER AND SETTERS
    public ShiftController getShiftController() {
        return shiftController;
    }

    public void setShiftController(ShiftController shiftController) {
        this.shiftController = shiftController;
    }

    public CommonFunctions getCommonFunctions() {
        return commonFunctions;
    }

    public void setCommonFunctions(CommonFunctions commonFunctions) {
        this.commonFunctions = commonFunctions;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public HumanResourceBean getHumanResourceBean() {
        return humanResourceBean;
    }

    public void setHumanResourceBean(HumanResourceBean humanResourceBean) {
        this.humanResourceBean = humanResourceBean;
    }

    public Roster getRoster() {
        return roster;
    }

    public void setRoster(Roster roster) {
        this.roster = roster;
    }

    public Long getDateRange() {
        return dateRange;
    }

    public void setDateRange(Long dateRange) {
        this.dateRange = dateRange;
    }

    public List<ShiftTable> getShiftTables() {
        return shiftTables;
    }

    public void setShiftTables(List<ShiftTable> shiftTables) {
        this.shiftTables = shiftTables;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public StaffShiftFacade getStaffShiftFacade() {
        return staffShiftFacade;
    }

    public void setStaffShiftFacade(StaffShiftFacade staffShiftFacade) {
        this.staffShiftFacade = staffShiftFacade;
    }

    public void visible() {
        all = true;
    }

    public void hide() {
        all = false;
    }

    public boolean isAll() {
        return all;
    }

    public void setAll(boolean all) {
        this.all = all;
    }

    public StaffShiftHistoryFacade getStaffShiftHistoryFacade() {
        return staffShiftHistoryFacade;
    }

    public void setStaffShiftHistoryFacade(StaffShiftHistoryFacade staffShiftHistoryFacade) {
        this.staffShiftHistoryFacade = staffShiftHistoryFacade;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public StaffShift getStaffShift() {
        return staffShift;
    }

    public void setStaffShift(StaffShift staffShift) {
        this.staffShift = staffShift;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public CommonController getCommonController() {
        return commonController;
    }

    public void setCommonController(CommonController commonController) {
        this.commonController = commonController;
    }

}
