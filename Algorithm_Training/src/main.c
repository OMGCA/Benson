#include <stdio.h>
#include <math.h>
#include "cgp-sls.h"

int main(void){

	struct parameters *params = NULL;
	struct dataSet *trainingData = NULL;
    struct dataSet *validationData = NULL;
    struct dataSet *testData = NULL;
	struct chromosome *chromo = NULL;

	int numInputs = 8;
	int numNodes = 60;
	int numOutputs = 9;
	int nodeArity = 4;

	int numGens = 46500;
	double targetFitness = 0.1;
	int updateFrequency = 500;

	params = initialiseParameters(numInputs, numNodes, numOutputs, nodeArity);

    setRandomNumberSeed(1234);

	addNodeFunction(params, "add,sub,mul,div,sin,pow,and,xnor");

	setTargetFitness(params, targetFitness);

    setMutationRate(params,0.1);

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

	saveChromosomeDot(chromo,0,"chromo.dot");

    int i;
    for(i = 0; i < getNumDataSetSamples(testData); i++)
    {
		int mismatchError = 0;
        executeChromosome(chromo,getDataSetSampleInputs(testData,i));
        double chromoOutputs[9] = {0,0,0,0,0,0,0,0,0};
        int j;
        for(j = 0; j < 9; j++){
            chromoOutputs[j] = getChromosomeOutput(chromo,j);
            printf("%.2f ",chromoOutputs[j]);
			if(abs(chromoOutputs[j] - getDataSetSampleOutputs(testData,i)[j]) > 0.5 )
				mismatchError++;
        }
		printf("Mismatches: %d", mismatchError);
        printf("\n");

    }

	freeDataSet(trainingData);
	freeChromosome(chromo);
	freeParameters(params);

	return 0;
}
