package com.tarsier.rule.proxy;

public class DBRuleProxy extends BaseRuleProxy {
//	public static final Logger		LOG				= LoggerFactory.getLogger(DBRuleProxy.class);
//	public static final String		DEFAULT_TOPIC	= "logs";
//	@Value("${mysql.url}")
//	private String					url;
//	@Value("${mysql.user}")
//	private String					user;
//	@Value("${mysql.password}")
//	private String					password;
//
//	private MysqlConnectionManager	mysql;
//
//	@PostConstruct
//	private void start() {
//		LOG.info("start to connect db,url:{}, user:{}, password:{}", url, user, password);
//		mysql = new MysqlConnectionManager(url, user, password);
//		mysql.start();
//	}
//
//	public void updateStatus(int id, boolean status) {
//		try {
//			mysql.ensureConnectionValid();
//			String updateSql = "update rule set disabled=" + (status ? "1" : "0") + " where id=" + id;
//			try (PreparedStatement state = mysql.getConnection().prepareStatement(updateSql)) {
//				int rows = state.executeUpdate(updateSql);
//				LOG.info("update rule, sql:{}, result:{}", updateSql, rows);
//			}
//		}
//		catch (Exception e) {
//			LOG.error(e.getMessage(), e);
//		}
//	}
//
//	public void deleteRule(int id) {
//		String delSql = "delete from rule where id=" + id;
//		try (PreparedStatement state = mysql.getConnection().prepareStatement(delSql)) {
//			int rows = state.executeUpdate(delSql);
//			LOG.info("delete rule, sql:{}, result:{}", delSql, rows);
//		}
//		catch (Exception e) {
//			LOG.error(e.getMessage(), e);
//		}
//	}
//
//	public void insertRule(Rule rule) {
//		StringBuilder insert = new StringBuilder();
//		insert.append("insert into rule(`name`,  `project_name`,  `channel`,  `filter`,  `group`,  `recover`,  `timewin`,");
//		insert.append("`trigger`,  `interval`,  `type`,  `persons`,  `date_range`,  `time_range`,  `weekly`,  `monthly`,  `disabled`,  `create_time`,`update_time`)");
//		insert.append(" values (");
//		insert.append("'").append(rule.getName()).append("'");
//		insert.append(",'").append(rule.getProjectName()).append("'");
//		insert.append(",'").append(rule.getChannel() == null ? DEFAULT_TOPIC : rule.getChannel()).append("'");
//		if (rule.getCondition().getFilter() == null) {
//			insert.append(",").append("null");
//		}
//		else {
//			insert.append(",'").append(rule.getCondition().getFilter()).append("'");
//		}
//
//		if (rule.getCondition().getGroup() == null) {
//			insert.append(",").append("null");
//		}
//		else {
//			insert.append(",'").append(rule.getCondition().getGroup()).append("'");
//		}
//
//		if (rule.getCondition().getRecover() == null) {
//			insert.append(",").append("null");
//		}
//		else {
//			insert.append(",'").append(rule.getCondition().getRecover()).append("'");
//		}
//		insert.append(",").append(rule.getCondition().getTimewin());
//
//		if (rule.getCondition().getTrigger() == null) {
//			insert.append(",").append("null");
//		}
//		else {
//			insert.append(",'").append(rule.getCondition().getTrigger()).append("'");
//		}
//
//		insert.append(",").append(rule.getNotification().getInterval());
//		insert.append(",").append(rule.getNotification().getType());
//		insert.append(",'").append(rule.getNotification().getPersons()).append("'");
//
//		Effective dtr = rule.getDateTimeRange();
//		if (dtr.getDateRange() != null) {
//			StringBuilder drv = new StringBuilder();
//			for (DateRange dr : dtr.getDateRange()) {
//				drv.append(dr.getStart_date()).append(":").append(dr.getEnd_date()).append(",");
//			}
//			if (drv.length() > 0) {
//				drv.deleteCharAt(drv.length() - 1);
//				insert.append(",'").append(drv.toString()).append("'");
//			}
//		}
//		else {
//			insert.append(",").append("null");
//		}
//
//		if (dtr.getTimeRange() != null) {
//			StringBuilder trv = new StringBuilder();
//			for (TimeRange tr : dtr.getTimeRange()) {
//				trv.append(tr.getStartHour()).append(":").append(tr.getEndHour()).append(",");
//			}
//			if (trv.length() > 0) {
//				trv.deleteCharAt(trv.length() - 1);
//				insert.append(",'").append(trv.toString()).append("'");
//			}
//		}
//		else {
//			insert.append(",").append("null");
//		}
//		if (dtr.getRepeat() != null) {
//			if (dtr.getRepeat().getWeekly() != null) {
//				StringBuilder weekly = new StringBuilder();
//				for (Integer w : dtr.getRepeat().getWeekly()) {
//					weekly.append(w).append(",");
//				}
//				if (weekly.length() > 0) {
//					weekly.deleteCharAt(weekly.length() - 1);
//					insert.append(",'").append(weekly.toString()).append("'");
//				}
//			}
//			else {
//				insert.append(",").append("null");
//			}
//			if (dtr.getRepeat().getMonthly() != null) {
//				StringBuilder monthly = new StringBuilder();
//				for (Integer m : dtr.getRepeat().getMonthly()) {
//					monthly.append(m).append(",");
//				}
//				if (monthly.length() > 0) {
//					monthly.deleteCharAt(monthly.length() - 1);
//					insert.append(",'").append(monthly.toString()).append("'");
//				}
//			}
//			else {
//				insert.append(",").append("null");
//			}
//		}
//		else {
//			insert.append(",").append("null");
//			insert.append(",").append("null");
//		}
//		insert.append(",").append(rule.getDisabled() == null ? 0 : rule.getDisabled());
//		insert.append(",'").append(DateUtil.getDate(new Date(), DateUtil.DATE_TIME)).append("'");
//		insert.append(",'").append(DateUtil.getDate(new Date(), DateUtil.DATE_TIME)).append("')");
//		try (PreparedStatement state = mysql.getConnection().prepareStatement(insert.toString())) {
//			int rows = state.executeUpdate(insert.toString());
//			LOG.info("insert rule, sql:{}, result:{}", insert.toString(), rows);
//		}
//		catch (Exception e) {
//			LOG.error(e.getMessage(), e);
//		}
//	}
//
//	public void updateRule(Rule rule) {
//		StringBuilder update = new StringBuilder("update rule set ");
//		update.append("`name`='").append(rule.getName()).append("'");
//		update.append(",project_name='").append(rule.getProjectName()).append("'");
//		update.append(",topic='").append(rule.getChannel() == null ? DEFAULT_TOPIC : rule.getChannel()).append("'");
//		if (rule.getCondition().getFilter() != null) {
//			update.append(",filter='").append(rule.getCondition().getFilter()).append("'");
//		}
//		else {
//			update.append(",filter=null");
//		}
//		if (rule.getCondition().getGroup() != null) {
//			update.append(",`group`='").append(rule.getCondition().getGroup()).append("'");
//		}
//		else {
//			update.append(",`group`=null");
//		}
//		if (rule.getCondition().getRecover() != null) {
//			update.append(",`recover`='").append(rule.getCondition().getRecover()).append("'");
//		}
//		else {
//			update.append(",`recover`=null");
//		}
//		if (rule.getCondition().getTrigger() != null) {
//			update.append(",`trigger`='").append(rule.getCondition().getTrigger()).append("'");
//		}
//		else {
//			update.append(",`trigger`=null");
//		}
//
//		update.append(",timewin=").append(rule.getCondition().getTimewin());
//		update.append(",`interval`=").append(rule.getNotification().getInterval());
//		update.append(",`type`=").append(rule.getNotification().getType());
//		update.append(",persons='").append(rule.getNotification().getPersons()).append("'");
//		if (rule.getDisabled() != null) {
//			update.append(",disabled=").append(rule.getDisabled());
//		}
//		if (rule.getDateTimeRange() != null) {
//			Effective dtr = rule.getDateTimeRange();
//			if (dtr.getDateRange() != null) {
//				StringBuilder drv = new StringBuilder();
//				for (DateRange dr : dtr.getDateRange()) {
//					drv.append(dr.getStart_date()).append(":").append(dr.getEnd_date()).append(",");
//				}
//				if (drv.length() > 0) {
//					drv.deleteCharAt(drv.length() - 1);
//					update.append(",date_range='").append(drv.toString()).append("'");
//				}
//			}
//			else {
//				update.append(",date_range=null");
//			}
//			if (dtr.getTimeRange() != null) {
//				StringBuilder trv = new StringBuilder();
//				for (TimeRange tr : dtr.getTimeRange()) {
//					trv.append(tr.getStartHour()).append(":").append(tr.getEndHour()).append(",");
//				}
//				if (trv.length() > 0) {
//					trv.deleteCharAt(trv.length() - 1);
//					update.append(",time_range='").append(trv.toString()).append("'");
//				}
//			}
//			else {
//				update.append(",time_range=null");
//			}
//
//			if (dtr.getRepeat() != null) {
//				if (dtr.getRepeat().getWeekly() != null) {
//					StringBuilder weekly = new StringBuilder();
//					for (Integer w : dtr.getRepeat().getWeekly()) {
//						weekly.append(w).append(",");
//					}
//					if (weekly.length() > 0) {
//						weekly.deleteCharAt(weekly.length() - 1);
//						update.append(",weekly='").append(weekly.toString()).append("'");
//					}
//				}
//				else {
//					update.append(",weekly=null");
//				}
//				if (dtr.getRepeat().getMonthly() != null) {
//					StringBuilder monthly = new StringBuilder();
//					for (Integer m : dtr.getRepeat().getMonthly()) {
//						monthly.append(m).append(",");
//					}
//					if (monthly.length() > 0) {
//						monthly.deleteCharAt(monthly.length() - 1);
//						update.append(",monthly='").append(monthly.toString()).append("'");
//					}
//				}
//				else {
//					update.append(",monthly=null");
//				}
//			}
//		}
//		update.append(" where id=").append(rule.getId());
//		LOG.info("update sql:{}", update.toString());
//		try (PreparedStatement state = mysql.getConnection().prepareStatement(update.toString())) {
//			int rows = state.executeUpdate(update.toString());
//			LOG.info("result:{}", update.toString(), rows);
//		}
//		catch (Exception e) {
//			LOG.error(e.getMessage(), e);
//		}
//	}
//
//	@Override
//	public List<Rule> listAll() {
//		return byStatus(null);
//	}
//
//	public List<Rule> listEnabled() {
//		return byStatus(false);
//	}
//
//	public List<Rule> byStatus(Boolean disabled) {
//		try {
//			String sql = "select * from rule ";
//			if (disabled != null) {
//				sql = sql + " where `disabled`=" + (disabled ? 1 : 0);
//			}
//			mysql.ensureConnectionValid();
//			try (PreparedStatement state = mysql.getConnection().prepareStatement(sql)) {
//				ResultSet set = state.executeQuery();
//				List<Rule> es = new ArrayList<>();
//				while (set.next()) {
//					Rule ef = new Rule();
//					ef.setId(set.getInt("id"));
//					ef.setName(set.getString("name"));
//					ef.setProjectName(set.getString("project_name"));
//					ef.setChannel(set.getString("topic"));
//
//					ef.setCondition(new Condition());
//					ef.getCondition().setFilter(set.getString("filter"));
//					ef.getCondition().setGroup(set.getString("group"));
//					ef.getCondition().setRecover(set.getString("recover"));
//					ef.getCondition().setTimewin(set.getInt("timewin"));
//					ef.getCondition().setTrigger(set.getString("trigger"));
//
//					ef.setNotification(new Notification());
//					ef.getNotification().setInterval(set.getInt("interval"));
//					ef.getNotification().setType(set.getInt("type"));
//					ef.getNotification().setPersons(set.getString("persons"));
//					ef.setDisabled(set.getInt("disabled"));
//
//					ef.setDateTimeRange(new Effective());
//					if (set.getString("date_range") != null) {
//						ef.getDateTimeRange().setDateRange(new ArrayList<DateRange>());
//						for (String dr : set.getString("date_range").split(",")) {
//							String[] startEnd = dr.split(":");
//							DateRange dateRange = new DateRange();
//							dateRange.setStart_date(startEnd[0]);
//							dateRange.setEnd_date(startEnd[1]);
//							ef.getDateTimeRange().getDateRange().add(dateRange);
//						}
//					}
//
//					if (set.getString("time_range") != null) {
//						ef.getDateTimeRange().setTimeRange(new ArrayList<TimeRange>());
//						for (String tr : set.getString("time_range").split(",")) {
//							String[] startEnd = tr.split(":");
//							TimeRange timeRange = new TimeRange();
//							timeRange.setStartHour(Integer.valueOf(startEnd[0]));
//							timeRange.setEndHour(Integer.valueOf(startEnd[1]));
//							ef.getDateTimeRange().getTimeRange().add(timeRange);
//						}
//					}
//
//					Repeat repeat = new Repeat();
//					ef.getDateTimeRange().setRepeat(repeat);
//					String weekly = set.getString("weekly");
//					if (weekly != null && weekly.length() > 0) {
//						repeat.setWeekly(new HashSet<Integer>());
//						for (String w : weekly.split(",")) {
//							repeat.getWeekly().add(Integer.valueOf(w));
//						}
//					}
//					String monthly = set.getString("monthly");
//					if (monthly != null && monthly.length() > 0) {
//						repeat.setMonthly(new HashSet<Integer>());
//						for (String m : monthly.split(",")) {
//							repeat.getMonthly().add(Integer.valueOf(m));
//						}
//					}
//					es.add(ef);
//				}
//				return es;
//			}
//		}
//		catch (Exception e) {
//			LOG.error(e.getMessage(), e);
//		}
//		return null;
//	}
//
//	@Override
//	public void reset() {
//		// 
//
//	}

}
