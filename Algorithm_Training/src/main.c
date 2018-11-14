#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#include "cgp-sls.h"

double simpleThresholdClassifier(struct parameters *params, struct chromosome *chromo, struct dataSet *data);

int main(void)
{

	struct parameters *params = NULL;
	struct dataSet *trainingData = NULL;
	struct dataSet *validationData = NULL;
	struct dataSet *testData = NULL;
	struct chromosome *chromo = NULL;

	int numInputs = 13;
	int numNodes = 20;
	int numOutputs = 1;
	int nodeArity = 4;

	int numGens = 100000;
	double targetFitness = 0.1;
	int updateFrequency = 500;

	params = initialiseParameters(numInputs, numNodes, numOutputs, nodeArity);

	setRandomNumberSeed(1234);

	addNodeFunction(params, "add,sub,mul,div,sin,pow,xor");

	setTargetFitness(params, targetFitness);

	setMutationRate(params, 0.1);

	setCustomFitnessFunction(params, simpleThresholdClassifier, "STC");

	setShortcutConnections(params, 0);

	setUpdateFrequency(params, updateFrequency);

	printParameters(params);

	// Note: you may need to check this path such that it is relative to your executable
	trainingData = initialiseDataSetFromFile("./01_training.csv");
	validationData = initialiseDataSetFromFile("./02_validation.csv");
	testData = initialiseDataSetFromFile("./03_test.csv");

	chromo = runValiTestCGP(params, trainingData, validationData, testData, numGens);

	printChromosome(chromo, 0);

	saveChromosomeDot(chromo, 0, "chromo.dot");

	int i;
	int mismatchError = 0;
	for (i = 0; i < getNumDataSetSamples(testData); i++)
	{
		
		executeChromosome(chromo, getDataSetSampleInputs(testData, i));
		double chromoOutputs = 0;

        chromoOutputs = getChromosomeOutput(chromo, 0);
		int expectedOutput = getDataSetSampleOutputs(testData, i)[0];
        printf("%.2f ", chromoOutputs);

		switch (expectedOutput){
			case 1:
				if(chromoOutputs > 1){
					mismatchError++;
					printf(" Mismatch ");
				}
				else
					printf(" Match ");
					
				break;
			case 2:
				if(chromoOutputs <= 1 || chromoOutputs > 2){
					mismatchError++;
					printf(" Mismatch ");
				}
				else
					printf(" Match ");
				break;
			case 3:
				if(chromoOutputs <= 2 || chromoOutputs > 3){
					mismatchError++;
					printf(" Mismatch ");
				}
				else
					printf(" Match ");
				break;
			case 4:
				if(chromoOutputs <= 3){
					mismatchError++;
					printf(" Mismatch ");
				}
				else
					printf(" Match ");
				break;
		}

		printf(" %.2f", getDataSetSampleOutputs(testData, i)[0]);
		printf("\n");
	}

	printf("Match Entities: %d\n", getNumDataSetSamples(testData) - mismatchError);
	printf("Mismatch Entities: %d", mismatchError);

	printf("\n");

	freeDataSet(trainingData);
	freeDataSet(validationData);
	freeDataSet(testData);
	freeChromosome(chromo);
	freeParameters(params);

	return 0;
}

double simpleThresholdClassifier(struct parameters *params, struct chromosome *chromo, struct dataSet *data)
{
	double thresHold[] = {1,2,3};

	int i;
	double threshError = 0;

	if (getNumChromosomeInputs(chromo) != getNumDataSetInputs(data))
	{
		printf("Error: the number of chromosome inputs must match the number of inputs specified in the dataSet.\n");
		printf("Terminating.\n");
		exit(0);
	}

	if (getNumChromosomeOutputs(chromo) != getNumDataSetOutputs(data))
	{
		printf("Error: the number of chromosome outputs must match the number of outputs specified in the dataSet.\n");
		printf("Terminating.\n");
		exit(0);
	}

	for (i = 0; i < getNumDataSetSamples(data); i++)
	{

		executeChromosome(chromo, getDataSetSampleInputs(data, i));
		double chromoOutput = getChromosomeOutput(chromo,0);
		int expectedClass = getDataSetSampleOutputs(data,i)[0];

		switch(expectedClass){
			case 1:
				if(chromoOutput > thresHold[0])
					threshError++;
				break;
			case 2:
				if(chromoOutput > thresHold[1] || chromoOutput <= thresHold[0])
					threshError++;
				break;
			case 3:
				if(chromoOutput > thresHold[2] || chromoOutput <= thresHold[1])
					threshError++;
				break;
			case 4:
				if(chromoOutput < thresHold[2])
					threshError++;
				break;
		}

	}

	return threshError / (getNumDataSetSamples(data));
}
