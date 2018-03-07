package com.tarsier.rule.proxy;

public class FileRuleProxy extends BaseRuleProxy {
//	private static final Logger	LOGGER	= LoggerFactory.getLogger(FileRuleProxy.class);
//	private Map<Integer, Rule>	rules	= new HashMap<Integer, Rule>();
//	@Value("${rule.file.path:/app/data/tarsier/rule}")
//	private String				path;
//
//	@Override
//	public List<Rule> listEnabled() {
//		List<Rule> rs = new ArrayList<Rule>();
//		for (Rule r : rules.values()) {
//			if (r.getDisabled() == 0) {
//				rs.add(r);
//			}
//		}
//		return rs;
//	}
//
//	@Override
//	public List<Rule> listAll() {
//		List<Rule> rs = new ArrayList<Rule>();
//		for (Rule r : rules.values()) {
//			rs.add(r);
//		}
//		return rs;
//
//	}
//
//	@Override
//	@PostConstruct
//	public void reset() {
//		File ruleFile = new File(path);
//		BufferedReader in=null;
//		try {
//			if (!ruleFile.exists()) {
//				ruleFile.createNewFile();
//			}
////			BufferedReader in = new BufferedReader(new FileReader(ruleFile));
//			in = new BufferedReader(new InputStreamReader(new FileInputStream(ruleFile),"UTF-8"));
//			StringBuffer buffer = new StringBuffer();
//			String line = "";
//			while ((line = in.readLine()) != null) {
//				buffer.append(line);
//			}
//			if(buffer.length()>0){
//				rules = JSON.parseObject(buffer.toString(), new TypeReference<Map<Integer, Rule>>() {});
//			}
//		}
//		catch (Exception e) {
//			throw new RuntimeException(e.getMessage() + path, e);
//		}
//		finally {
//			IOUtils.close(in);
//		}
//	}
//
//	private void flash() {
//		String jsonString = JSON.toJSONString(rules);
//		File ruleFile = new File(path);
//		FileWriter writer=null;
//		try {
//			writer = new FileWriter(ruleFile);
//			writer.write(jsonString);
//		}
//		catch (IOException e) {
//			LOGGER.error(e.getMessage(), e);
//		}
//		finally {
//			IOUtils.close(writer);
//		}
//	}
//	@Override
//	public void insertRule(Rule rule) {
//		rules.put(rule.getId(), rule);
//		flash();
//	}
//
//	@Override
//	public void updateRule(Rule rule) {
//		rules.put(rule.getId(), rule);
//		flash();
//	}
//
//	@Override
//	public void deleteRule(int id) {
//		rules.remove(id);
//		flash();
//	}
//
//	@Override
//	public void updateStatus(int id, boolean status) {
//		rules.get(id).setDisabled(status ? 1 : 0);
//		flash();
//	}

}
