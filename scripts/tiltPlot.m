dataPath = 'Benson_Data\Patients\';
addpath(dataPath);
listing = dir(strcat(dataPath, '*.txt'));

for c = 1:size(listing)
    % Get list of file name
    sampleName = listing(c).name;
    
    % Load data
    Data = load(sampleName);
    
    % Use try/catch to avoid empty data that could terminate the script
    try
        xTilt = Data(:,4);
        yTilt = Data(:,5);

        timeStamp = Data(:,1);
       
        % Plot x and y tilt data again timestamp
        plot(timeStamp,xTilt);
        hold on;
        plot(timeStamp,yTilt);
        
        % Figure configuration
        title(sampleName);
        xlabel('Time Stamp') 
        ylabel('Tilt');
        legend('X Tilt','Y Tilt');
        
        %Clear canvas for next plotting
        hold off;
        
        % resize the figure
        set(gcf, 'Position', [200 200 1000 350])
        
        %Save file
        baseFile = strcat(erase(strcat(sampleName), ".txt"),'_tilt.png');
        newFileName = strcat(dataPath,baseFile);
        saveas(gcf,newFileName);
    catch
        disp('Empty data.');
        disp('Execution will continue.');
    end
end
