grammar Rule;
@header {
package com.tarsier.antlr;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import org.apache.commons.lang.NumberUtils;
}
@members{ 
    
	private static final Logger LOGGER = LoggerFactory.getLogger(RuleParser.class);
    public Map<String, Object> functionMap = new HashMap<String, Object>();
    // seconds
    private int           pastSecond;

    private long                startSecond;
    private long                sysStartSecond=System.currentTimeMillis()/1000;

    private Cache<Long, Map<String, Double>> cache;

    private Map<String, String>           msg;
    
    public void setPastSecond(int pastSecond){
    	this.pastSecond = pastSecond;
    }
    
    public void setCache(Cache<Long, Map<String, Double>> cache) {
        this.cache = cache;
    }
    
    public void setMsg(Map<String, String> msg, long second) {
        this.msg = msg;
        this.startSecond = second - pastSecond;
        functionMap.clear();
    }
    
    public void resetInput(){
    	_input.seek(0);
    }

    private Double sum(String fName) {
        Double value = null;
        ConcurrentMap<Long, Map<String, Double>> asMap = cache.asMap();
        Set<Long> keys = asMap.keySet();
        for (Long key : keys) {
            if (startSecond <= key) {
            	if(value == null){
            		value = 0.0;
            	}
                value = value + asMap.get(key).get(fName);
            }
        }

        return value;
    }

	private Double avg(String fName) {
	    double sum = sum("sum_"+fName);
	    return sum == 0 ? 0 : sum / count("count_"+fName);
	}

    private long count(String fName) {
        Double value = 0.0;
        ConcurrentMap<Long, Map<String, Double>> asMap = cache.asMap();
        Set<Long> keys = asMap.keySet();
        for (Long key : keys) {
            if (startSecond <= key) {
                value = value + asMap.get(key).get(fName);
            }
        }
        return value.longValue();
    }
    
    private Double max(String fName){
    	Double value = null;
    	ConcurrentMap<Long, Map<String, Double>> asMap = cache.asMap();
        Set<Long> keys = asMap.keySet();
        for (Long key : keys) {
            if (startSecond <= key) {
                double max = asMap.get(key).get(fName);
                if(value == null || max > value){
                	value = max;
                }
            }
        }
        return value;
    }
    
    private Double min(String fName){
    	Double value = null;
    	ConcurrentMap<Long, Map<String, Double>> asMap = cache.asMap();
        Set<Long> keys = asMap.keySet();
        for (Long key : keys) {
            if (startSecond <= key) {
                double min = asMap.get(key).get(fName);
                if(value == null || min < value){
                	value = min;
                }
            }
        }
        return value;
    }
}

stat returns[boolean value]
	 : logical=logicalExpr 															{	$value = $logical.value;
	 																					//LOGGER.debug("stat expr:{}, value:{}",$logical.text,$value);
	 																				}
	 ;

logicalExpr returns[boolean value]
					: '(' logical=logicalExpr ')'									{	$value = $logical.value;
																						//LOGGER.debug("({}), value:{}",$logical.text, $value);
																					}
					| calculator=calculatorExpr										{	String v=String.valueOf($calculator.value);$value = NumberUtils.isNumber(v) ? NumberUtils.createDouble(v) !=0 : Boolean.valueOf(v);
																						//LOGGER.debug("calculator->logical {}, value:{}",$calculator.text, $value);
																					}
					| relation=relationExpr											{	$value = $relation.value;
																						//LOGGER.debug("relation->logical {}, value:{}",$relation.text, $value);
																					}
					| '!' logical=logicalExpr										{	$value = !$logical.value;
																						//LOGGER.debug("! {}, value:{}",$logical.text, $value);
																					}
					| logical1=logicalExpr op=('&&' | '||') logical2=logicalExpr 	{	$value = $op.text.equals("&&") ? ($logical1.value && $logical2.value) : ($logical1.value || $logical2.value);
																						//LOGGER.debug("{} {} {}, value:{}",$logical1.text, $op.text, $logical2.text, $value);
																					}
					;
					
relationExpr returns[boolean value]
	:	'@'	n1=calculatorExpr														{	$value=msg.containsKey($n1.text);
																						//LOGGER.debug("@ n1:{}, value:{}",$n1.text, $value);
																					}
	|	'!@'	n1=calculatorExpr													{	$value=!msg.containsKey($n1.text);
																						//LOGGER.debug("!@ n1:{}, value:{}",$n1.text, $value);
																					}
	|	n1=calculatorExpr '#' n2=calculatorExpr										{	String lv = $n1.value == null ? "" : $n1.value.toString().toLowerCase();String rv = $n2.value == null ? "" : $n2.value.toString().toLowerCase();$value=lv.contains(rv);
																						//LOGGER.debug("# n1:{},n2:{}, value:{}",$n1.text, $n2.text, $value);
																					}
	|	n1=calculatorExpr '!#' n2=calculatorExpr									{	String lv = $n1.value == null ? "" : $n1.value.toString().toLowerCase();String rv = $n2.value == null ? "" : $n2.value.toString().toLowerCase();$value=!lv.contains(rv);
																						//LOGGER.debug("!# n1:{},n2:{}, value:{}",$n1.text, $n2.text, $value);
																					}
	|	n1=calculatorExpr '~' n2=calculatorExpr										{	$value= $n1.value == null ? false : Pattern.compile($n2.value.toString()).matcher($n1.value.toString()).find();
																						//LOGGER.debug("~ n1:{},n2:{}, value:{}",$n1.text, $n2.text, $value);
																					}
	|	n1=calculatorExpr '<'  n2=calculatorExpr									{	String v1=String.valueOf($n1.value);
																						String v2=String.valueOf($n2.value);
																						if(NumberUtils.isNumber(v1) && NumberUtils.isNumber(v2)){
																							$value=Double.parseDouble(v1)<Double.parseDouble(v2);
																						}
																						//LOGGER.debug("< n1:{},n2:{}, value:{}",$n1.text, $n2.text, $value);
																					}
	|	n1=calculatorExpr '<='  n2=calculatorExpr									{	String v1=String.valueOf($n1.value);
																						String v2=String.valueOf($n2.value);
																						if(NumberUtils.isNumber(v1) && NumberUtils.isNumber(v2)){
																							$value=Double.parseDouble(v1)<=Double.parseDouble(v2);
																						}
																						//LOGGER.debug("<= n1:{},n2:{}, value:{}",$n1.text, $n2.text, $value);
																					}
	|	n1=calculatorExpr '>'  n2=calculatorExpr									{	String v1=String.valueOf($n1.value);
																						String v2=String.valueOf($n2.value);
																						if(NumberUtils.isNumber(v1) && NumberUtils.isNumber(v2)){
																							$value=Double.parseDouble(v1)>Double.parseDouble(v2);
																						}
																						//LOGGER.debug("> n1:{},n2:{}, value:{}",$n1.text, $n2.text, $value);	
																					}
	|	n1=calculatorExpr '>='  n2=calculatorExpr									{	String v1=String.valueOf($n1.value);
																						String v2=String.valueOf($n2.value);
																						if(NumberUtils.isNumber(v1) && NumberUtils.isNumber(v2)){
																							$value=Double.parseDouble(v1)>=Double.parseDouble(v2);
																						}
																						//LOGGER.debug(">= n1:{},n2:{}, value:{}",$n1.text, $n2.text, $value);		
																					}	
	|	n1=calculatorExpr '=' n2=calculatorExpr										{	String v1=String.valueOf($n1.value);
																						String v2=String.valueOf($n2.value);
																						if(NumberUtils.isNumber(v1) && NumberUtils.isNumber(v2)){
																							$value=Double.parseDouble(v1)==Double.parseDouble(v2);
																						}
																						else {
																							$value= v1==null ? v2==null : v1.equalsIgnoreCase(v2);
																						}
																						//LOGGER.debug("= n1:{},n2:{}, value:{}",$n1.text, $n2.text, $value);	
																					}	
	|	n1=calculatorExpr '!=' n2=calculatorExpr									{	$value=!($n1.value==null ? $n2.value==null : String.valueOf($n1.value).equalsIgnoreCase(String.valueOf($n2.value)));
																						//LOGGER.debug("!= n1:{},n2:{}, value:{}",$n1.text, $n2.text, $value);	
																					}
	;
    
calculatorExpr returns[Object value]
	:	'(' calculator=calculatorExpr ')'											{	$value = $calculator.value;
																						//LOGGER.debug("({}), value:{}",$calculator.text, $value);
																					}
	|	atomic=atomicExpr															{	$value = $atomic.value;
																						//LOGGER.debug("atomic:{}, value:{}",$atomic.text, $value);
																					}	
	|	n1=calculatorExpr op=('*' | '/')  n2=calculatorExpr							{	double d1=Double.parseDouble(String.valueOf($n1.value));
																						double d2=Double.parseDouble(String.valueOf($n2.value));
																						$value = $op.text.equals("*") ? d1*d2 : d1/d2;
																						//LOGGER.debug("*/ n1:{},n2:{}, value:{}",$n1.text, $n2.text, $value);	
																					}	
	|   n1=calculatorExpr op=('+' | '-')  n2=calculatorExpr							{	String v1=String.valueOf($n1.value);
																						String v2=String.valueOf($n2.value);
																						if(NumberUtils.isNumber(v1) && NumberUtils.isNumber(v2)){
																							double d1=Double.parseDouble(v1); 
																							double d2=Double.parseDouble(v2);
																							$value = $op.text.equals("+") ? d1+d2 : d1-d2;
																						}
																						else {
																							$value= $op.text.equals("+") ? v1+v2 : v1.replaceAll(v2,"");
																						}
																						//LOGGER.debug("+- n1:{},n2:{}, value:{}",$n1.text, $n2.text, $value);	
																					}	
	|	'-' n2=calculatorExpr														{	$value = -Double.parseDouble(String.valueOf($n2.value));
																						//LOGGER.debug("- n2:{}, value:{}",$n2.text, $value);	
																					}	
	;

atomicExpr returns[Object value]
	: str=STRING																	{	String v= $str.text.substring(1,$str.text.length()-1);$value=v.replaceAll("\\\\\"","\"");}
	| key=KEY																		{	$value = msg.get($key.text);}
	| num=INT																		{	$value = Long.parseLong($num.text);}
	| dou=DOUBLE																	{	$value = Double.parseDouble($dou.text);}
	| fun=FUNCTION_INVOKE															{	String fName = $fun.text.replaceAll(" ", "").toLowerCase();
																						if(cache == null && msg == null){$value=fName;}
																						else if(fName.startsWith("count(")){$value=count(fName);} 
																						else if(fName.startsWith("sum(")){$value=sum(fName);} 
																						else if(fName.startsWith("avg(")){$value=avg(fName);}
																						else if(fName.startsWith("max(")){$value=max(fName);}
																						else if(fName.startsWith("min(")){$value=min(fName);}
																						else {throw new RuntimeException("not support function:"+fName);}
																						functionMap.put($fun.text, $value);
																						//LOGGER.debug("call function {}, value is {}", fName, $value);
																					}
	;
//CONTAIN	:   '#'	|   'contain'	;
//REGEXPR	:   '~'	|   'regexpr'	;
//EXIST : '@';
STRING :  '"' (ESC|.)*? '"';
FUNCTION_INVOKE : KEY (WS)*'(' ( (WS)* KEY|INT(WS)* (','(WS)*KEY|INT(WS)*)*)? (WS)*')';
KEY	:  [A-Za-z_@]+[A-Za-z0-9_@.-]*;
INT	:  [0-9]+;
DOUBLE :  (INT+ '.' INT);
WS	 :   [ \t\n\r]+ -> skip ;
ESC	:   '\\"' 
	   |   '\\\\' 
	   ; 
