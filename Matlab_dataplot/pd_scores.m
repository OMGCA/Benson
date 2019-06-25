[pdSheet, sheetHeader] = xlsread("PD.xlsx");
pdStageCol = zeros(52,1);
for c = 1:52
    pdStageCol(c) = pdSheet(c,size(pdSheet,2));
end

pdStage = [1 2 3];
pdCounts = [19 7 26];

pdStageDes = ["PD-NC" "PD-MCI" "PD-D"];

% for c = 1:49
%     pdDataCol = attriReg(pdStageCol,pdSheet,c+3);
%     dataGap = max(max(pdDataCol)) - min(min(pdDataCol));
%     if c < 15
%         pdDataTier = [floor(dataGap/3+0.5) floor(dataGap*2/3+0.5)];
%     elseif c == 25
%         pdDataTier = [0.3348 - 0.15 0.3348 + 0.15];  
%     else
%         pdDataTier = [dataGap/3 dataGap*2/3];
%     end
%    
%     pdDataTierC = tierCount(pdCounts,pdDataCol,pdDataTier);
%     pdTitle = strcat(num2str(c)," ",sheetHeader(c+3));
%     
%     pdBar(pdDataTierC,pdDataTier,pdTitle,1);
% end

% 17 MoCA: 20 26
% 18 & 35 BensonTime Copy: 35000 65000
% 19 BensonLength Copy: 4200 5200
% 20 BensonSize Copy: 160000 200000
% 21 BensonRatio Copy: 0.33 0.37
% 22 BensonVelSD Copy: 2.5 3.2
% 23 BensonAngSD Copy: 1.2 1.6
% 24 BensonPenOff Copy: 0.33 0.41
% 25 BensonHoriPor Copy: 0.37 0.42
% 26 BensonVertPor Copy: 0.19 0.22
% 27 BensonObliPor Copy: 0.34 0.4
% 28 BensonHoriSD Copy: 0.2 0.275
% 29 BensonVertSD Copy: 0.3 0.375
% 30 BensonObliSD Copy: 0.285 0.4
% 36 BensonLength Recall: 3500 6000
% 37 BensonSize Recall:

tmpIndex = 11;
range1 = 1;
range2 = 4;
hardRangeSwitch = 1;
pdDataCol = attriReg(pdStageCol,pdSheet,tmpIndex+3);
dataGap = max(max(pdDataCol)) - min(min(pdDataCol));
if hardRangeSwitch == 1
    pdDataTier = [range1 range2]; 
else
    pdDataTier = [dataGap/3 dataGap*2/3];
end

pdDataTierC = tierCount(pdCounts,pdDataCol,pdDataTier);
pdTitle = strcat(num2str(tmpIndex)," ",sheetHeader(tmpIndex+3));
figure(4);
pdBar(pdDataTierC,pdDataTier,pdTitle,1);