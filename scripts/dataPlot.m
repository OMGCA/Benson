control = load('control.csv');
patient = load('patient.csv');

% Column 1: Total time | 2: Total length | 3: Size | 4: Aspect ratio | 5: Velocity SD | 6: Angular SD
% Column 7: Pen-off % | 8: Hori portion | 9: Vert portion | 10: Obli portion | 11: Hori SD | 12: Vert SD | 13: Obli Sd
% Column 14: Hesitation count | 15: Hesitation portion | 16: Score
controlSamples = length(control(:,1));
patientSamples = length(patient(:,1));
%Time
controlTime = control(:,1);
patientTime = patient(:,1);
timeTierCount = 8;
timeTier = (max(max(controlTime),max(patientTime)) - min(min(controlTime),min(patientTime))) / timeTierCount;

% Counting entities in different tiers in control and patient - Time

controlTimeTier = zeros(timeTierCount,1);
for c = 1:size(controlTime)
    for d = 1:timeTierCount
        if(controlTime(c,1) < timeTier * d && controlTime(c,1) >= timeTier * (d-1))
            controlTimeTier(d,1) = controlTimeTier(d,1) + 1;
        end
    end
end
for d = 1:timeTierCount
    controlTimeTier(d,1) = controlTimeTier(d,1) / controlSamples;
end

patientTimeTier = zeros(timeTierCount,1);
for c = 1:size(patientTime)
    for d = 1:timeTierCount
        if(patientTime(c,1) < timeTier * d && patientTime(c,1) >= timeTier * (d-1))
            patientTimeTier(d,1) = patientTimeTier(d,1) + 1;
        end
    end
end
for d = 1:timeTierCount
    patientTimeTier(d,1) = patientTimeTier(d,1) / patientSamples;
end

figure(1);
timeBar = [controlTimeTier patientTimeTier];
bar(timeBar);
title('Distribution in tiers in total drawing time');
xlabel('Tier');
ylabel('Entries');
legend('Control','Patient');

% Aspect Ratio
controlRatio = control(:,4);
patientRatio = patient(:,4);
ratioTierNumber = 5;
ratioTier = (max(max(controlRatio),max(patientRatio)) - min(min(controlRatio),min(patientRatio))) / ratioTierNumber;

controlRatioTier = zeros(ratioTierNumber,1);
patientRatioTier = zeros(ratioTierNumber,1);

for c = 1:size(controlRatio)
    for d = 1:ratioTierNumber
        if(controlRatio(c,1) < ratioTier * d && controlRatio(c,1) >= ratioTier*(d-1))
            controlRatioTier(d,1) = controlRatioTier(d,1) + 1;
        end
    end
end

for d = 1:ratioTierNumber
    controlRatioTier(d,1) = (controlRatioTier(d,1) / controlSamples);
end

for c = 1:size(patientRatio)
    for d = 1:ratioTierNumber
        if(patientRatio(c,1) < ratioTier * d && patientRatio(c,1) >= ratioTier*(d-1))
            patientRatioTier(d,1) = patientRatioTier(d,1) + 1;
        end
    end 
end
for d = 1:ratioTierNumber
    patientRatioTier(d,1) = patientRatioTier(d,1) / patientSamples;
end

figure(2);
ratioBar = [controlRatioTier patientRatioTier];
bar(ratioBar);
title('Distribution in tiers in aspect ratio');
xlabel('Tier');
ylabel('Entries');
legend('Control','Patient');