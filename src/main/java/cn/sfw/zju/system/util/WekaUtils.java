package cn.sfw.zju.system.util;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.swing.JFrame;

import weka.classifiers.Classifier;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.trees.M5P;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.experiment.InstanceQuery;
import weka.filters.Filter;
import weka.filters.supervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import weka.filters.unsupervised.attribute.Standardize;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;

public class WekaUtils {
	private static WekaUtils utils = null;
	
	private WekaUtils(){}
	
	public static synchronized  WekaUtils getInstance(){
		if(utils==null){
			utils=new WekaUtils();
		}
		return utils;
	}
	
	/**
	 * get instances from db by sql
	 * @return
	 */
	public Instances connectDB(){
		InstanceQuery query;
		Instances instances = null;
		try {
			query = new InstanceQuery();
			query.setDebug(true);
			query.setUsername("C##TEST");
			query.setPassword("test");
			query.setDatabaseURL("jdbc:oracle:thin:@127.0.0.1:1521:ORCL");
			String sql="SELECT M,D,W,IS_HOLIDAYS,R1,R2,R3,R7,A3,R FROM INTERVAL_D WHERE CST_ID=5681";
			query.setQuery(sql);
			instances=query.retrieveInstances();
			instances.setClassIndex(9);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return instances;
	}
	
	/**
     * generate weka dataSource file
     * @param instances weka Instances
     */
    public void generateArffFile(Instances instances, String path) {
        ArffSaver saver = new ArffSaver();
        saver.setInstances(instances);
        try {
            saver.setFile(new File(path));
            saver.writeBatch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * get instances form the arff file
     * @param path
     * @return
     */
    public Instances getInstancesFromArffFile(String path){
    	DataSource source;
    	Instances data = null;
		try {
			source = new DataSource(path);
			data = source.getDataSet();
			if (data.classIndex() == -1){
				data.setClassIndex(data.numAttributes() - 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return data;
    }
	
    /**
	 * get the m5p classifer
	 * @param instances
	 * @param options{-N,-U,-R,-M,-L} 经测试{-N,-M,6}效果�?�?
	 * @return
	 * @throws Exception
	 */
	public M5P getM5PClassifer(Instances instances,String[] options) throws Exception{
		M5P classifier= new M5P();
	    classifier.setOptions(options);
	    classifier.buildClassifier(instances);
		return classifier;
	}
	
	
	/**
	 * get the bp classifer
	 * @param instances
	 * @param options{-L(0-1,0.3),-M(0-1,0.2),-N(500),-V(0-100,0),-S(0),-E(20),-D}
	 * @return
	 * @throws Exception
	 */
	public MultilayerPerceptron getBPClassifer(Instances instances,String[] options) throws Exception{
		MultilayerPerceptron classifier= new MultilayerPerceptron();
	    classifier.setOptions(options);
	    classifier.buildClassifier(instances);
		return classifier;
	}
	
	/**
	 * get the smo classifer
	 * @param instances
	 * @param options
	 * @return
	 * @throws Exception
	 */
	public SMOreg getSMOregClassifer(Instances instances,String[] options) throws Exception{
		SMOreg classifier= new SMOreg();
		classifier.setOptions(options);
		classifier.buildClassifier(instances);
		return classifier;
	}
    
	/**
	 * 缺失值处�?
	 * @param instances
	 * @return
	 * @throws Exception
	 */
	public Instances replaceMissingV(Instances instances) throws Exception{
		ReplaceMissingValues missingValues = new ReplaceMissingValues();
		missingValues.setInputFormat(instances);
		Instances inst=Filter.useFilter(instances, missingValues);
		return inst;
	}
		
	/**
	 * 标准�?
	 * @param instances
	 * @return
	 * @throws Exception
	 */
	public Instances standardizeV(Instances instances) throws Exception{
		Standardize standardize = new Standardize();
		standardize.setInputFormat(instances);
		Instances inst=Filter.useFilter(instances, standardize);
		return inst;
	}
		
	/**
	 * 规范�?
	 * @param instances
	 * @return
	 * @throws Exception
	 */
	public Instances normalizeV(Instances instances) throws Exception{
		Normalize normalize= new Normalize();
		normalize.setInputFormat(instances);
		Instances inst=Filter.useFilter(instances, normalize);
		return inst;
	}
		
	/**
	 * 标称值转化为二分�?
	 * @param instances
	 * @return
	 * @throws Exception
	 */
	public Instances toBinary(Instances instances) throws Exception{
		NominalToBinary nominalToBinary = new NominalToBinary();
		nominalToBinary.setBinaryAttributesNominal(false);
		nominalToBinary.setInputFormat(instances);
		String[] options = new String[1]; 
		options[0] = "-A"; 
		nominalToBinary.setOptions(options);
		Instances inst=Filter.useFilter(instances, nominalToBinary);
		return inst;		
	}
    
	/**
	 * 决策树模型可视化
	 * @param newClassifier
	 * @throws Exception
	 */
	public void  treeVisual(M5P newClassifier) throws Exception{
		TreeVisualizer treeVisualizer=new TreeVisualizer(null, newClassifier.graph(), new PlaceNode2());
				JFrame jFrame=new JFrame("决策树测�?:M5P");
				jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				jFrame.setSize(1980, 1024);
				jFrame.getContentPane().setLayout(new BorderLayout());
				jFrame.getContentPane().add(treeVisualizer, BorderLayout.CENTER);
				jFrame.setVisible(true);
				treeVisualizer.fitToScreen();
	}
	
	
	
}
