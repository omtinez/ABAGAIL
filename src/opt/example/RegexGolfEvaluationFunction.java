package opt.example;

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
     * Map a given double to a typeable character. 
     * Instead of dealing with probability distributions, 
     * we make some chars like letters more likely here 
     */
    private char mapDoubleToChar(double d) {
    	if (d < 32.0) return '\0';
    	else if (d > 126.0) return (char)(((int) d) % 25 + 97);
    	else return (char)((int) d);
    }
    
    /**
     * Make a new regex golf evaluation function
     * @param p the set of words that we must match (positive)
     * @param n the set of words that we shouldn't match (negative)
     */
    public RegexGolfEvaluationFunction(String[] p, String[] n) {
    	matchPosList = p;
    	matchNegList = n;
    }

    /**
     * @see opt.EvaluationFunction#value(opt.OptimizationData)
     */
    public double value(Instance d) {
    	int value = 0;
        Vector data = d.getData();
        
        // First unit in vector tells us index of word
        String w = matchPosList[(int) (matchPosList.length * data.get(0) / 256)];
        
        // Second and third units tell us start/end substr index
        int sub0 = (int) ((w.length() - 1) * data.get(1) / 256);
        int sub1 = (int) ((w.length() - 1 - sub0) * data.get(2) / 256) + 1 + sub0;

        // Fourth unit tells us index of character to replace
        int ix = (int) ((sub1 - sub0) * data.get(3) / 256);
        
        // Fifth unit tells us character to replace by
        char c = mapDoubleToChar(data.get(4));
        StringBuilder sb = new StringBuilder();        
        sb.append(w.substring(sub0, sub1)).replace(ix, ix, String.valueOf(c));
        
        Matcher m;
        try {
        	m = Pattern.compile(sb.toString()).matcher("");
        } catch (PatternSyntaxException e) {
        	return (double) Integer.MIN_VALUE;
        }
        for (String s : matchPosList) {
        	if (m.reset(s).find()) value += 10;
        }
        for (String s : matchNegList) {
        	if (m.reset(s).find()) value -= 10;
        }
        return (double) value - sb.length();
    }
}