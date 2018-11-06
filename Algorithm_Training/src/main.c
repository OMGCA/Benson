<<<<<<< HEAD
#include <stdio.h>
#include "cgp-sls.h"

int main(void){

	struct parameters *params = NULL;
	struct dataSet *trainingData = NULL;
    struct dataSet *validationData = NULL;
    struct dataSet *testData = NULL;
	struct chromosome *chromo = NULL;

	int numInputs = 5;
	int numNodes = 50;
	int numOutputs = 9;
	int nodeArity = 5;

	int numGens = 100000;
	double targetFitness = 0.1;
	int updateFrequency = 500;

	params = initialiseParameters(numInputs, numNodes, numOutputs, nodeArity);

    setRandomNumberSeed(1234);

	addNodeFunction(params, "add,sub,mul,div,sin,sqrt,pow,exp,and,xnor");

	setTargetFitness(params, targetFitness);

    setMutationRate(params,0.08);
    
    setShortcutConnections(params,0);

    setNumThreads(params,2);

	setUpdateFrequency(params, updateFrequency);

	printParameters(params);

	// Note: you may need to check this path such that it is relative to your executable 
	trainingData = initialiseDataSetFromFile("./01_training.csv");
    validationData = initialiseDataSetFromFile("./02_validation.csv");
    testData = initialiseDataSetFromFile("./03_test.csv");

	chromo = runValiTestCGP(params,trainingData,validationData,testData,numGens);

	printChromosome(chromo, 0);

    int i;
    for(i = 0; i < getNumDataSetSamples(testData); i++)
    {
        executeChromosome(chromo,getDataSetSampleInputs(testData,i));
        double chromoOutputs[8] = {0,0,0,0,0,0,0,0};
        int j;
        for(j = 0; j < 8; j++){
            chromoOutputs[j] = getChromosomeOutput(chromo,j);
            printf("%.2f ",chromoOutputs[j]);
        }
        printf("\n");
        
    }

	freeDataSet(trainingData);
	freeChromosome(chromo);
	freeParameters(params);

	return 0;
}
=======
#include <stdio.h>
#include "cgp-sls.h"

int main(void){

	struct parameters *params = NULL;
	struct dataSet *trainingData = NULL;
    struct dataSet *validationData = NULL;
    struct dataSet *testData = NULL;
	struct chromosome *chromo = NULL;

	int numInputs = 5;
	int numNodes = 50;
	int numOutputs = 9;
	int nodeArity = 5;

	int numGens = 100000;
	double targetFitness = 0.1;
	int updateFrequency = 500;

	params = initialiseParameters(numInputs, numNodes, numOutputs, nodeArity);

    setRandomNumberSeed(1234);

	addNodeFunction(params, "add,sub,mul,div,sin,sqrt,pow,exp,and,xnor");

	setTargetFitness(params, targetFitness);

    setMutationRate(params,0.08);
    
    setShortcutConnections(params,0);

    setNumThreads(params,2);

	setUpdateFrequency(params, updateFrequency);

	printParameters(params);

	// Note: you may need to check this path such that it is relative to your executable 
	trainingData = initialiseDataSetFromFile("./01_training.csv");
    validationData = initialiseDataSetFromFile("./02_validation.csv");
    testData = initialiseDataSetFromFile("./03_test.csv");

	chromo = runValiTestCGP(params,trainingData,validationData,testData,numGens);

	printChromosome(chromo, 0);

    int i;
    for(i = 0; i < getNumDataSetSamples(testData); i++)
    {
        executeChromosome(chromo,getDataSetSampleInputs(testData,i));
        double chromoOutputs[8] = {0,0,0,0,0,0,0,0};
        int j;
        for(j = 0; j < 8; j++){
            chromoOutputs[j] = getChromosomeOutput(chromo,j);
            printf("%.2f ",chromoOutputs[j]);
        }
        printf("\n");
        
    }

	freeDataSet(trainingData);
	freeChromosome(chromo);
	freeParameters(params);

	return 0;
}
>>>>>>> 1229decac5352a7e36b46e1be8fe10a5c814f8bb
