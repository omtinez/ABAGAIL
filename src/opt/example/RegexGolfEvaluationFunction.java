package opt.example;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import opt.EvaluationFunction;
import shared.Instance;
import util.linalg.Vector;

/**
 * A regex golf evaluation function
 * @author Oscar Martinez <omtinez@gatech.edu>
 * @version 0.1
 */
public class RegexGolfEvaluationFunction implements EvaluationFunction {

	/**
     * The words that we must match
     */
    private String[] matchPosList;
    
    /**
     * The words that we shouldn't match
     */
    private String[] matchNegList;
    
    /**
     * The set of regex building blocks to be used
     */
    private String[] rexComponents;
    
    /**
     * Map a given double to a typeable character. 
     * Instead of dealing with probability distributions, 
     * we make some chars like letters more likely here 
     */
    public static char mapDoubleToChar(double d) {
    	if (d < 32.0) return '\0';
    	else if (d > 126.0) return (char)(((int) d) % 25 + 97);
    	else return (char)((int) d);
    }
    
    /**
     * Map the given parameter vector to regex string
     */
    public String mapParamsToRegex(Instance d) {
    	Vector data = d.getData();
    	StringBuilder sb = new StringBuilder();
    	for (int i = 0; i < data.size(); i++) {
    		if (data.get(i) > 0 && rexComponents[i] != null) {
    			sb.append("|").append(rexComponents[i]);
    		}
    	}
    	
        return sb.length() == 0 ? "" : sb.toString().substring(1);
    }
    
    /**
     * Make a new regex golf evaluation function
     * @param p the set of words that we must match (positive)
     * @param n the set of words that we shouldn't match (negative)
     * @param c the set of regex building blocks to be used
     */
    public RegexGolfEvaluationFunction(String[] p, String[] n, String[] c) {
    	matchPosList = p;
    	matchNegList = n;
    	rexComponents = c;    	
    	for(int i = 0; i < rexComponents.length; i++) {
    		Matcher m = Pattern.compile(rexComponents[i]).matcher("");
    		for (String s : matchNegList) {
    			if (m.reset(s).find()) {
    				rexComponents[i] = null;
    				break;
    			}
    		}
    	}
    }

    /**
     * @see opt.EvaluationFunction#value(opt.OptimizationData)
     */
    public double value(Instance d) {
    	int value = 0;
        String pat = mapParamsToRegex(d);
        
        Matcher m;
        try {
        	m = Pattern.compile(pat).matcher("");
        } catch (PatternSyntaxException e) {
        	return (double) Integer.MIN_VALUE;
        }
        for (String s : matchPosList) {
        	if (m.reset(s).find()) value += 10;
        }
        for (String s : matchNegList) {
        	if (m.reset(s).find()) value -= 10;
        }
        return (double) value - pat.length();
    }
}