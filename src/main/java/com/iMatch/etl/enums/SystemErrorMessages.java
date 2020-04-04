package com.iMatch.etl.enums;

/**
 * Hexagon Global IT Services (ALL RIGHTS RESERVED)
 * Created with IntelliJ IDEA.
 * User: mayankk
 * Date: 17/12/12, 3:17 PM
 */
public enum SystemErrorMessages {

    ASSET_CLASS_NOT_FOUND(1, MessageType.ERROR, "trademgr.notfound.assetClass") ,
    BENEFIT_TYPE_NOT_FOUND(2, MessageType.ERROR, "trademgr.notfound.benefitType") ,
    BANK_ACCOUNT_NOT_FOUND(3, MessageType.ERROR, "cashmgr.notfound.bankAccount") ,
    CURRENCY_NOT_FOUND(4, MessageType.ERROR, "currency.notfound"),
    GL_BALANCE_ACCOUNT_NOT_FOUND(5,MessageType.ERROR, "cashmgr.notfound.glBalance"),
    ISSUE_TYPE_NOT_FOUND(6,MessageType.ERROR, "trademgr.notfound.issueType"),
    INV_REASON_NOT_FOUND(7, MessageType.ERROR, "trademgr.notfound.invReason"),
    INV_REASON_NOT_MATCHING(8, MessageType.ERROR, "trademgr.notfound.invReasonMatching"),
    PORTFOLIO_BANK_NOT_FOUND(9, MessageType.ERROR, "trademgr.notfound.portfolioBank"),
    PORTFOLIO_NOT_FOUND(10, MessageType.ERROR, "trademgr.notfound.portfolio"),
    PORTFOLIO_CLASS_NOT_FOUND(11, MessageType.ERROR, "trademgr.notfound.portfolioclass"),
    REFERENCE_CURRENCY_RATE_NOT_FOUND(12,MessageType.ERROR,"core.reference.notfound.currency"),
    RELATED_PARTY_NOT_FOUND(13,MessageType.ERROR, "trademgr.notfound.relatedParty"),
    SECURITY_NOT_FOUND(14, MessageType.ERROR, "trademgr.notfound.security"),
    SECURITY_DTL_NOT_FOUND(15, MessageType.ERROR, "trademgr.notfound.securityDtl"),
    SECURITY_VALCHG_NOT_FOUND(16, MessageType.ERROR, "trademgr.notfound.securityValchg"),
    SYSTEM_DEFAULT_NOT_FOUND(17, MessageType.ERROR, "trademgr.notfound.systemDefault"),
    TRANS_CODE_NOT_FOUND(18, MessageType.ERROR, "trademgr.notfound.transMaster"),
    TRANS_CODE_DEFAULT_NOT_FOUND(19, MessageType.ERROR, "trademgr.notfound.transCodeDefault"),
    TRADE_DTL_TRF_NOT_FOUND(20, MessageType.ERROR, "trademgr.notfound.tradeDtlTrf"),
    HOLDING_DETAILS_NOT_FOUND(21, MessageType.ERROR, "holdingmgr.notfound.holdingDtl"),
    IND_SECTOR_NOT_FOUND(22,MessageType.ERROR,"mastermgr.notfound.indSector"),
    BENCHMARK__NOT_FOUND(23,MessageType.ERROR,"mastermgr.notfound.benchmark"),
    SECTOR_SEC_TYPE_NOT_FOUND(24,MessageType.ERROR,"mastermgr.notfound.securitySecType") ,
    HOLDING_LOT_DETAILS_NOT_FOUND(25,MessageType.ERROR,"holdingmgr.notfound.holdingLot"),
    COUNTRY_NOT_FOUND(26,MessageType.ERROR,"country.notfound"),
    SECURITY_MAP_NOT_FOUND(27, MessageType.ERROR, "trademgr.notfound.securityMapping"),
    RELATED_PARTY_DATA_NOT_FOUND(28,MessageType.ERROR, "trademgr.notfound.relatedPartyData"),
    RESPONSE_RECEIVED(29,MessageType.ERROR,"taskmgr.response.received.already"),
    RATING_DATA_DTLS_NOT_FOUND(30,MessageType.ERROR,"mastermgr.notfound.ratingData"),
    REPORT_SERVER_CONNECTION_EXCEPTION(31, MessageType.ERROR, "unable.to.connect.to.pentaho"),
    REPORT_NO_MAPPING(32, MessageType.ERROR, "unable.to.find.report.mapping"),
    REVIEW_APPROVER_NOT_FOUND(33, MessageType.ERROR, "review.approver.notfound"),
    ETL_FLOW_MISSING(34, MessageType.ERROR, "etl.flow.missing"),
    ETL_PASSWORD_MISMATCH(35, MessageType.ERROR, "etl.password.nomatch"),
    GRIDVIEW_COL_MISMATCH(36, MessageType.ERROR, "gridview.col.mismatch"),
    DYNAMIC_VIEW_CONFIG_ERROR(37, MessageType.ERROR, "dynamicview.config.error"),
    DYNAMIC_VIEW_CONFIG_MASTER_ERROR(38, MessageType.ERROR, "dynamicview.masterconfig.error"),
    //19/10/16 chandu: new enum GL_ACCOUNT_NOT_FOUND
    GL_ACCOUNT_NOT_FOUND(39, MessageType.ERROR, "glaccount.notfound.error"),
    EXCHANGE_CYCLE(51, MessageType.ERROR, "exchangeCycleCannotBeNull"),
    FIX_MESSAGE_MAPPING_NOT_FOUND(41,MessageType.ERROR,"fixMessageMappingNotFound"),
    FIX_SESSION_NOT_CONNECTED(42,MessageType.ERROR,"fixSessionNotConnected" ),
    COUNTRY_TYPE_NOT_FOUND(43, MessageType.ERROR, "countryTypePairNotFound"),
    //28/11/16 hexniranjan: new enum TRANSFEREE_CONDITION
    TRANSFEREE_CONDITION(44, MessageType.ERROR, "transferee.condition.not.avaliable"),
    RULE_MOVEMENT_NOT_REC_PROCESSED(40, MessageType.ERROR, "ruleMovement.not.recProcessed"),
    REC_STATUS_SHOULD_NOT_BE_NULL(49,MessageType.ERROR , "recStatus.shouldNotBeNull"),
    RETURNS_MORE_THAN_ONE_VALUE(50,MessageType.ERROR, "returns.more.thanOneValue"),
    TRANS_CODE_DEFAULT_MAPPING_NOT_FOUND(52, MessageType.ERROR, "transCode.notfound.transCodeDefault"),
    FIX_MESSAGE_NOT_SENT(53,MessageType.ERROR,"fixMessageNotSent" ),
    FEE_DEF_NOT_FOUND(54, MessageType.ERROR, "fee.def.not.found"),

    MASTER_CHANGED_RULE_REBUILD_PENDING(46, MessageType.ERROR, "master.changed.rule.rebuild.pending"),
    COLUMN_PRECISION(47,MessageType.ERROR,"column.precision"),
    BUILD_COMPLIANCE_PROCESS_PENDING(48, MessageType.ERROR, "build.compliance.process.pending"),
    WEB_SERVICE_CALL_FAILED(55, MessageType.ERROR, "ws.call.failed"),
    RETRY_EXCEPTION(56, MessageType.ERROR, "retry.required"),
    RATING_MASTER_NOT_FOUND(57,MessageType.ERROR,"ratingMasterConfig.notfound");
    private MessageType _messageType;
	private String _messageKey;
	private String _remedyMessageKey;
	private String _messageCode;

	private static final String MESSAGE_CODE_PREFIX = "HEX-S";
	private static final String MESSAGE_KEY_PREFIX = "system";
	private static final String REMEDY_MESSAGE_KEY_SUFFIX = "remedy";

	private SystemErrorMessages(long messageCode, MessageType messageType, String messageKey) {
		_messageCode = String.format("%s%05d",MESSAGE_CODE_PREFIX, messageCode);
		_messageType = messageType;
		_messageKey = String.format("%s.%05d.%s",MESSAGE_KEY_PREFIX,messageCode, messageKey);
		_remedyMessageKey = String.format("%s.%s",messageKey,REMEDY_MESSAGE_KEY_SUFFIX);
	}

	public MessageType getMessageType() {
		return _messageType;
	}

	public String getMessageKey() {
		return _messageKey;
	}

	public String getMessageCode() {
		return _messageCode;
	}

	public String getRemedyMessageKey() {
		return _remedyMessageKey;
	}
}
