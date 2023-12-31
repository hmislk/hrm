/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.data;

/**
 *
 * @author www.divudi.com
 */
public enum Privileges {

    //Main Menu Privileges
    TheaterIssueBHT,
    Opd,
    Inward,
    Lab,
    Pharmacy,
    Payment,
    Hr,
    Reports,
    User,
    Admin,
    Channelling,
    Clinical,
    Store,
    Search,
    CashTransaction,
    //Submenu Privileges
    OpdBilling,
    OpdCollectingCentreBillingMenu,
    OpdCollectingCentreBilling,
    OpdCollectingCentreBillSearch,
    OpdPreBilling,
    OpdPreBillSearch,
    OpdPreBillAccept,
    OpdBillSearch,
    OpdBillItemSearch,
    OpdReprint,
    OpdCancel,
    OpdReturn,
    OpdReactivate,
    OpdBillSearchEdit,
    InwardAdmissions,
    InwardAdmissionsAdmission,
    InwardAdmissionsEditAdmission,
    InwardAdmissionsInwardAppoinment,
    InwardRoom,
    InwardRoomRoomOccupency,
    InwardRoomRoomChange,
    InwardRoomGurdianRoomChange,
    InwardRoomDischarge,
    InwardServicesAndItems,
    InwardServicesAndItemsAddServices,
    InwardServicesAndItemsAddOutSideCharges,
    InwardServicesAndItemsAddProfessionalFee,
    InwardServicesAndItemsAddTimedServices,
    InwardBilling,
    InwardBillingInterimBill,
    InwardBillingInterimBillSearch,
    InwardSearch,
    InwardSearchServiceBill,
    InwardSearchProfessionalBill,
    InwardSearchFinalBill,
    InwardReport,
    InwardFinalBillReportEdit,
    InwardAdministration,
    InwardAdditionalPrivilages,
    InwardBillSearch,
    InwardBillItemSearch,
    InwardBillReprint,
    InwardCancel,
    InwardReturn,
    InwardReactivate,
    InwardCheck,
    InwardUnCheck,
    InwardFinalBillCancel,
    InwardOutSideMarkAsUnPaid,
    ShowInwardFee,
    InwardPharmacyMenu,
    InwardPharmacyIssueRequest,
    InwardPharmacyIssueRequestSearch,
    InwardBillSettleWithoutCheck,
    LabBilling,
    LabBillCancelSpecial,
    LabBillRefundSpecial,
    LabCasheirBillSearch,
    LabCashier,
    LabBillSearchCashier,
    LabBillSearch,
    LabBillItemSearch,
    LabBillCancelling,
    CollectingCentreCancelling,
    LabBillReturning,
    LabBillReprint,
    LabBillRefunding,
    LabBillReactivating,
    LabSampleCollecting,
    LabSampleReceiving,
    LabReportFormatEditing,
    LabDataentry,
    LabAutherizing,
    LabDeAutherizing,
    LabRevertSample,
    LabPrinting,
    LabReprinting,
    LabReportEdit,
    LabSummeries,
    LabSummeriesLevel1,
    LabSummeriesLevel2,
    LabSummeriesLevel3,
    LabReportSearchOwn,
    LabReportSearchAll,
    LabReceive,
    LabEditPatient,
    LabInvestigationFee,
    LabAddInwardServices,
    LabReportSearchByLoggedInstitution,
    IncomeReport,
    LabReport,
    DuesAndAccess,
    CheckEnteredData,
    LabAdiministrator,
    LabReports,
    LabItems,
    LabItemFeeUpadate,
    LabItemFeeDelete,
    LabLists,
    LabSetUp,
    LabInwardBilling,
    LabInwardSearchServiceBill,
    LabCollectingCentreBilling,
    LabCCBilling,
    LabCCBillingSearch,
    LabReportSearch,
    LabReportSearchOut,
    LabReportSearchOutPrintColor,
    LabReportSearchOutPrintBNW,
    LabReporting,
    //dont remove
    LabSearchBillLoggedInstitution,
    PaymentBilling,
    PaymentBillSearch,
    PaymentBillReprint,
    PaymentBillCancel,
    PaymentBillRefund,
    PaymentBillReactivation,
    ReportsSearchCashCardOwn,
    ReportsSearchCreditOwn,
    ReportsItemOwn,
    ReportsSearchCashCardOther,
    ReportSearchCreditOther,
    ReportsItemOther,
    PharmacyOrderCreation,
    PharmacyOrderApproval,
    PharmacyOrderCancellation,
    PharmacySale,
    PharmacySaleReprint,
    PharmacySaleCancel,
    PharmacySaleReturn,
    //Wholesale
    PharmacySaleWh,
    PharmacySaleReprintWh,
    PharmacySaleCancelWh,
    PharmacySaleReturnWh,
    //end wholesale
    PharmacyInwardBilling,
    PharmacyInwardBillingCancel,
    PharmacyInwardBillingReturn,
    PharmacyGoodReceive,
    //Wholesale
    PharmacyGoodReceiveWh,
    //end Wholesale
    PharmacyGoodReceiveCancel,
    PharmacyGoodReceiveReturn,
    PharmacyGoodReceiveEdit,
    PharmacyPurchase,
    //Wholesale
    PharmacyPurchaseWh,
    //Whalesale
    PharmacyPurchaseReprint,
    PharmacyPurchaseCancellation,
    PharmacyPurchaseReturn,
    PharmacyStockAdjustment,
    PharmacyStockAdjustmentSingleItem,
    PharmacyReAddToStock,
    PharmacyStockIssue,
    PharmacyDealorPayment,
    PharmacySearch,
    PharmacyReports,
    PharmacyTransfer,
    PharmacySummery,
    PharmacyAdministration,
    PharmacySetReorderLevel,
    PharmacyReturnWithoutTraising,
    PharmacyBHTIssueAccept,
    //theater
    Theatre,
    TheatreAddSurgery,
    TheatreBilling,
    TheaterTransfer,
    TheaterTransferRequest,
    TheaterTransferIssue,
    TheaterTransferRecieve,
    TheaterTransferReport,
    TheaterReports,
    TheaterSummeries,
    TheaterIssue,
    TheaterIssuePharmacy,
    TheaterIssueStore,
    TheaterIssueStoreBhtBilling,
    TheaterIssueStoreBhtSearchBill,
    TheaterIssueStoreBhtSearchBillItem,
    TheaterIssueOpd,
    TheaterIssueOpdForCasheir,
    TheaterIssueOpdSearchPreBill,
    TheaterIssueOpdSearchPreBillForReturnItemOnly,
    TheaterIssueOpdSearchPreBillReturn,
    TheaterIssueOpdSearchPreBillAddToStock,
    ClinicalPatientSummery,
    ClinicalPatientDetails,
    ClinicalPatientPhoto,
    ClinicalVisitDetail,
    ClinicalVisitSummery,
    ClinicalHistory,
    ClinicalAdministration,
    ClinicalPatientDelete,
    ChannelAdd,
    ChannelCancel,
    ChannelRefund,
    ChannelReturn,
    ChannelView,
    ChannelDoctorPayments,
    ChannelDoctorPaymentCancel,
    ChannelViewHistory,
    ChannelCreateSessions,
    ChannelManageSessions,
    ChannelAdministration,
    ChannelAgencyReports,
    AdminManagingUsers,
    AdminInstitutions,
    AdminStaff,
    AdminItems,
    AdminPrices,
    AdminFilterWithoutDepartment,
    ChangeProfessionalFee,
    ChangeCollectingCentre,
    StoreIssue,
    StoreIssueInwardBilling,
    StoreIssueSearchBill,
    StoreIssueBillItems,
    StorePurchase,
    StorePurchaseOrder,
    StorePurchaseOrderApprove,
    StorePurchaseOrderApproveSearch,
    StorePurchaseGRNRecive,
    StorePurchaseGRNReturn,
    StorePurchasePurchase,
    StoreTransfer,
    StoreTransferRequest,
    StoreTransferIssue,
    StoreTransferRecive,
    StoreTransferReport,
    StoreAdjustment,
    StoreAdjustmentDepartmentStock,
    StoreAdjustmentStaffStock,
    StoreAdjustmentPurchaseRate,
    StoreAdjustmentSaleRate,
    StoreDealorPayment,
    StoreDealorPaymentDueSearch,
    StoreDealorPaymentDueByAge,
    StoreDealorPaymentPayment,
    StoreDealorPaymentPaymentGRN,
    StoreDealorPaymentPaymentGRNSelect,
    StoreDealorPaymentGRNDoneSearch,
    StoreSearch,
    StoreReports,
    StoreSummery,
    StoreAdministration,
    SearchGrand,
    CashTransactionCashIn,
    CashTransactionCashOut,
    CashTransactionListToCashRecieve,
    ChannellingChannelBooking,
    ChannellingFutureChannelBooking,
    ChannellingPastBooking,
    ChannellingBookedList,
    ChannellingDoctorLeave,
    ChannellingDoctorLeaveByDate,
    ChannellingDoctorLeaveByServiceSession,
    ChannellingChannelSheduling,
    ChannellingChannelShedulRemove,
    ChannellingChannelShedulName,
    ChannellingChannelShedulStartingNo,
    ChannellingChannelShedulRoomNo,
    ChannellingChannelShedulMaxRowNo,
    ChannellingChannelAgentFee,
    ChannellingDoctorSessionView,
    ChannellingPayment,
    ChannellingPaymentPayDoctor,
    ChannellingPaymentDueSearch,
    ChannellingPaymentDoneSearch,
    ChannellingApoinmentNumberCountEdit,
    ChannellingEditSerialNo,
    ChannellingEditPatientDetails,
    ChannellingPrintInPastBooking,
    ChannellingEditCreditLimitUserLevel,
    ChannellingEditCreditLimitAdminLevel,
    ChannelReports,
    ChannelSummery,
    ChannelManagement,
    ChannelAgencyAgencies,
    ChannelAgencyCreditLimitUpdate,
    ChannelAgencyCreditLimitUpdateBulk,
    ChannelAddChannelBookToAgency,
    ChannelManageSpecialities,
    ChannelManageConsultants,
    ChannelEditingAppoinmentCount,
    ChannelAddChannelingConsultantToInstitutions,
    ChannelFeeUpdate,
    ChannelCrdeitNote,
    ChannelCrdeitNoteSearch,
    ChannelDebitNote,
    ChannelDebitNoteSearch,
    ChannelCashCancelRestriction,
    ChannelBookingChange,
    ChannelBookingBokking,
    ChannelBookingReprint,
    ChannelBookingCancel,
    ChannelBookingRefund,
    ChannelBookingSettle,
    ChannelBookingSearch,
    ChannelBookingViews,
    ChannelBookingDocPay,
    ChannelBookingRestric,
    ChannelCashierTransaction,
    ChannelCashierTransactionIncome,
    ChannelCashierTransactionIncomeSearch,
    ChannelCashierTransactionExpencess,
    ChannelCashierTransactionExpencessSearch,
    ChannelActiveVat,
    
    MemberShip,
    MembershipSchemes,
    MemberShipInwardMemberShip,
    MemberShipInwardMemberShipSchemesDicounts,
    MemberShipInwardMemberShipInwardMemberShipReport,
    MemberShipOpdMemberShipDis,
    MemberShipOpdMemberShipDisByDepartment,
    MemberShipOpdMemberShipDisByCategory,
    MemberShipOpdMemberShipDisOpdMemberShipReport,
    MemberShipMemberDeActive,
    MemberShipMemberReActive,
    
    HrAdmin,
    HrReports,
    HrReportsLevel1,
    HrReportsLevel2,
    HrReportsLevel3,
    EmployeeHistoryReport,
    hrDeleteLateLeave,
    HrGenerateSalary,
    HrGenerateSalarySpecial,
    HrAdvanceSalary,
    HrPrintSalary,
    HrWorkingTime,
    HrRosterTable,
    HrUploadAttendance,
    HrAnalyseAttendenceByRoster,
    HrAnalyseAttendenceByStaff,
    HrForms,
    HrLeaveForms,
    HrAdditionalForms,
    HrEditRetiedDate,
    HrRemoveResignDate,
    
    Developers,
    //Cashier
    AllCashierSummery,
    //Administration
    SearchAll,
    ChangePreferece,
    SendBulkSMS,

}
