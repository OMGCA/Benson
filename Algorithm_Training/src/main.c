#include <stdio.h>
#include <math.h>
#include "cgp-sls.h"

double simpleThresholdClassifier(struct parameters *params, struct chromosome *chromo, struct dataSet *data);

int main(void)
{

	struct parameters *params = NULL;
	struct dataSet *trainingData = NULL;
	struct dataSet *validationData = NULL;
	struct dataSet *testData = NULL;
	struct chromosome *chromo = NULL;

	int numInputs = 8;
	int numNodes = 15;
	int numOutputs = 9;
	int nodeArity = 2;

	int numGens = 200000;
	double targetFitness = 0.1;
	int updateFrequency = 500;

	params = initialiseParameters(numInputs, numNodes, numOutputs, nodeArity);

	setRandomNumberSeed(1234);

	addNodeFunction(params, "add,sub,mul,div");

	setTargetFitness(params, targetFitness);

	setMutationRate(params, 0.08);

	setCustomFitnessFunction(params, simpleThresholdClassifier, "STC");

	setShortcutConnections(params, 0);

	setNumThreads(params, 2);

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
	for (i = 0; i < getNumDataSetSamples(testData); i++)
	{
		int mismatchError = 0;
		executeChromosome(chromo, getDataSetSampleInputs(testData, i));
		double chromoOutputs[9] = {0, 0, 0, 0, 0, 0, 0, 0, 0};
		int j;
		for (j = 0; j < 9; j++)
		{
			chromoOutputs[j] = getChromosomeOutput(chromo, j);
			printf("%.2f ", chromoOutputs[j]);
			if (fabs(chromoOutputs[j] - getDataSetSampleOutputs(testData, i)[j]) >= 0.5)
				mismatchError++;
		}
		printf("Mismatches: %d", mismatchError);
		printf("\n");
	}

	printf("\n");

	for (i = 0; i < getNumDataSetSamples(trainingData); i++)
	{
		int mismatchError = 0;
		executeChromosome(chromo, getDataSetSampleInputs(trainingData, i));
		double chromoOutputs[9] = {0, 0, 0, 0, 0, 0, 0, 0, 0};
		int j;
		for (j = 0; j < 9; j++)
		{
			chromoOutputs[j] = getChromosomeOutput(chromo, j);
			printf("%.2f ", chromoOutputs[j]);
			if (fabs(chromoOutputs[j] - getDataSetSampleOutputs(trainingData, i)[j]) >= 0.5)
				mismatchError++;
		}
		printf("Mismatches: %d", mismatchError);
		printf("\n");
	}

	printf("\n");

	for (i = 0; i < getNumDataSetSamples(validationData); i++)
	{
		int mismatchError = 0;
		executeChromosome(chromo, getDataSetSampleInputs(validationData, i));
		double chromoOutputs[9] = {0, 0, 0, 0, 0, 0, 0, 0, 0};
		int j;
		for (j = 0; j < 9; j++)
		{
			chromoOutputs[j] = getChromosomeOutput(chromo, j);
			printf("%.2f ", chromoOutputs[j]);
			if (fabs(chromoOutputs[j] - getDataSetSampleOutputs(validationData, i)[j]) >= 0.5)
				mismatchError++;
		}
		printf("Mismatches: %d", mismatchError);
		printf("\n");
	}

	freeDataSet(trainingData);
	freeDataSet(validationData);
	freeDataSet(testData);
	freeChromosome(chromo);
	freeParameters(params);

	return 0;
}

double simpleThresholdClassifier(struct parameters *params, struct chromosome *chromo, struct dataSet *data)
{

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
		int singleMismatch = 0;

		executeChromosome(chromo, getDataSetSampleInputs(data, i));
		double chromoOutputs[9] = {0, 0, 0, 0, 0, 0, 0, 0, 0};

		int j;
		for (j = 0; j < 9; j++)
		{
			chromoOutputs[j] = getChromosomeOutput(chromo, j);
			if (fabs(chromoOutputs[j] - getDataSetSampleOutputs(data, i)[j]) >= 0.5)
				singleMismatch++;
		}

		if (singleMismatch == 0)
			threshError += 0;
		else if (singleMismatch > 0 && singleMismatch < 4)
			threshError++;
		else if (singleMismatch >= 4 && singleMismatch < 7)
			threshError += 2;
		else if (singleMismatch >= 7)
			threshError += 3;
		else
			threshError += 0;
	}

	return threshError / (getNumDataSetSamples(data) * 3);
}
