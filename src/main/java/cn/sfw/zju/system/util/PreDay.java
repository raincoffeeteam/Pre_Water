package cn.sfw.zju.system.util;

import java.util.Random;

import weka.attributeSelection.WrapperSubsetEval;
import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.meta.CVParameterSelection;
import weka.classifiers.trees.M5P;
import weka.core.Instances;
import weka.core.Utils;

public class PreDay {
	//private static Logger logger = Logger.getLogger(PreDay.class);
	
	public static void main(String[] args) throws Exception {
		/*Instances instances=toBinary(normalizeV(standardizeV(replaceMissingV(connectDB()))));
		instances.randomize(new Random());
		Instances  train = new Instances(instances, 0);
		Instances test =new Instances(instances, 0);	
		
		for(int i=0;i<instances.numInstances();i++){
			if(i<instances.numInstances()/2){
				train.add(instances.get(i));
			}else{
				test.add(instances.get(i));
			}
		}  */
		WekaUtils utils = WekaUtils.getInstance();
		String path=System.getProperty("user.dir");
		Instances train = utils.getInstancesFromArffFile(path+"/train.arff");
		Instances test = utils.getInstancesFromArffFile(path+"/test.arff");
		
		
		System.out.println("支持向量机回�?");   
		CVParameterSelection cvParameterSelection1 = new CVParameterSelection();
		String[] options1={};
		Classifier smo= utils.getSMOregClassifer(train, options1);
		cvParameterSelection1.setClassifier(smo);
		cvParameterSelection1.addCVParameter("C 0.1 2 20");
		cvParameterSelection1.setNumFolds(5);
		cvParameterSelection1.buildClassifier(train);
		System.out.println(Utils.joinOptions(cvParameterSelection1.getBestClassifierOptions()));
		String[] bestOptions1=cvParameterSelection1.getBestClassifierOptions();
		Classifier newsmo=utils.getSMOregClassifer(train, bestOptions1);
			
		Evaluation smoTrainEval = new Evaluation(train);
		smoTrainEval.evaluateModel(newsmo, train);
		System.out.println(smoTrainEval.toSummaryString());
			
		Evaluation smoTestEval = new Evaluation(test);
		smoTestEval.evaluateModel(newsmo, test);
		System.out.println(smoTestEval.toSummaryString());
		
		System.out.println("决策树算�?");
	    CVParameterSelection cvParameterSelection= new CVParameterSelection();
	    String[] options={"-N"};
		Classifier classifier= utils.getM5PClassifer(train, options);
		cvParameterSelection.setClassifier(classifier);
		cvParameterSelection.addCVParameter("M 1 20 20");
		cvParameterSelection.setNumFolds(10);
		cvParameterSelection.buildClassifier(train);
		System.out.println("�?优参数："+Utils.joinOptions(cvParameterSelection.getBestClassifierOptions()));
		String[] bestOptions=cvParameterSelection.getBestClassifierOptions();
		Classifier newClassifier= utils.getM5PClassifer(train, bestOptions);
		   
		Evaluation eval = new Evaluation(train);
		eval.evaluateModel(newClassifier, train);
		System.out.println("train:"+eval.toSummaryString());
		   
		Evaluation eval2 = new Evaluation(test);
		eval2.evaluateModel(newClassifier, test);
		System.out.println("test:"+eval2.toSummaryString());
		//utils.treeVisual((M5P)newClassifier);
		
		System.out.println("神经网络");
        CVParameterSelection cvParameterSelection2= new CVParameterSelection();
	    String[] options2={};
	    Classifier bp= utils.getBPClassifer(train, options2);
	    cvParameterSelection2.setClassifier(bp);
		//cvParameterSelection.addCVParameter("L 0.1 1 10");
		//cvParameterSelection.addCVParameter("M 0.1 1 10");
		cvParameterSelection2.setNumFolds(10);
		cvParameterSelection2.buildClassifier(train);
		System.out.println(Utils.joinOptions(cvParameterSelection2.getBestClassifierOptions()));
		String[] bestOptions2=cvParameterSelection2.getBestClassifierOptions();
		
		Classifier newbp= utils.getBPClassifer(train, bestOptions2);
		
		Evaluation bptrain = new Evaluation(train);
		bptrain.evaluateModel(newbp, train);
		System.out.println(bptrain.toSummaryString());
		
		Evaluation bptest = new Evaluation(test);
		bptest.evaluateModel(newbp, test);
		System.out.println(bptest.toSummaryString());
			
	}
}
