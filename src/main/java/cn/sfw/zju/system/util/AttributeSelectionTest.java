package cn.sfw.zju.system.util;

import java.util.Enumeration;
import java.util.Random;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.attributeSelection.WrapperSubsetEval;
import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.meta.AttributeSelectedClassifier;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;

public class AttributeSelectionTest {
	static WekaUtils utils = WekaUtils.getInstance();
	 /**
	  * use the filter to select attribute  
	  * @param data
	  * @throws Exception
	  */
	  protected static void useFilter(Instances data) throws Exception {  
	    System.out.println("\n2. Filter");  
	    weka.filters.supervised.attribute.AttributeSelection filter = new weka.filters.supervised.attribute.AttributeSelection();  
	    CfsSubsetEval eval = new CfsSubsetEval();
	    GreedyStepwise search = new GreedyStepwise();  
	    search.setSearchBackwards(true);  
	    filter.setEvaluator(eval);  
	    filter.setSearch(search);  
	    filter.setInputFormat(data); 
	    System.out.println( "number of instance attribute = " + data.numAttributes() );
	    Instances newData = Filter.useFilter(data, filter); 
	    System.out.println( "number of selected instance attribute = " + newData.numAttributes() );
	    Enumeration<Attribute>  s=newData.enumerateAttributes();
	    System.out.println();
	    while (s.hasMoreElements())
	        System.out.println(s.nextElement());
	    System.out.println(data.get(0));  
	  }  
	  
	  protected static void useWrapperSubsetEval(Instances date) throws Exception{
		  WrapperSubsetEval wrapperSubsetEval = new WrapperSubsetEval();
	  }
	  
	  /** 
	   * uses the low level approach 
	   */  
	  protected static void useLowLevel(Instances data) throws Exception {  
	    System.out.println("\n3. Low-level");  
	    AttributeSelection attsel = new AttributeSelection();  
	    CfsSubsetEval eval = new CfsSubsetEval();
	    GreedyStepwise search = new GreedyStepwise();  
	    search.setSearchBackwards(true);  
	    attsel.setEvaluator(eval);  
	    attsel.setSearch(search);  
	    attsel.SelectAttributes(data);  
	    int[] indices = attsel.selectedAttributes();
	    for(int i:indices){
	    	System.out.println(data.attribute(i));
	    }
	    System.out.println("selected attribute indices (starting with 0):\n" + Utils.arrayToString(indices));  
	  } 
	  
	  /** 
	   * uses the meta-classifier 
	   */  
	  protected static void useClassifier(Instances data) throws Exception {  
	    System.out.println("\n1. Meta-classfier");  
	    AttributeSelectedClassifier classifier = new AttributeSelectedClassifier();  
	    CfsSubsetEval eval = new CfsSubsetEval();  
	    GreedyStepwise search = new GreedyStepwise();  
	    search.setSearchBackwards(true);  
	    Classifier base= utils.getSMOregClassifer(data, new String[]{});
	    classifier.setClassifier(base);  
	    classifier.setEvaluator(eval);  
	    classifier.setSearch(search);  
	    Evaluation evaluation = new Evaluation(data);  
	    evaluation.crossValidateModel(classifier, data, 10, new Random(1));  
	    System.out.println(evaluation.toSummaryString());  
	  } 
	  
	  public static void main(String[] args) throws Exception {  
	      
		  
		  	String path=System.getProperty("user.dir");		        
		    // load data  
		    System.out.println("\n0. Loading data");  
		    Instances data = utils.connectDB();
		    if (data.classIndex() == -1)  
		      data.setClassIndex(data.numAttributes() - 1);  
		  
		    // 1. meta-classifier  
		   // useClassifier(data);  
		  
		    // 2. filter  
		    useFilter(data);  
		  
		    // 3. low-level  
		    useLowLevel(data);  
		    
		    
		  }  
}
