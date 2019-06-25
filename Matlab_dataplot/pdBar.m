function pdBar(pdAttri,pdTier,pdTitle,isSave)
    pdStageDes = ["PD-NC" "PD-MCI" "PD-D"];
    bar(pdAttri);
    hold on;
    
    %plot(1:length(pdAttri(:,1)),pdAttri(:,1),'LineWidth',1.5,'Color',[0,0.51,0.84]);
    %plot(1:length(pdAttri(:,2)),pdAttri(:,2),'LineWidth',1.5,'Color',[0.85,0.37,0.098]);
    %plot(1:length(pdAttri(:,3)),pdAttri(:,3),'LineWidth',1.5,'Color',[0.93,0.69,0.13]);
    tierColor = [[0,0.51,0.84] [0.85,0.37,0.098] [0.93,0.69,0.13]];
    
    tmpPoint = [[] [] []];
    for c = 1:3
        for d = 1:3
            tmpNumber = sprintf('%.2f',pdAttri(c,d));
            text(0.775+(d-1)*0.225+c-1,pdAttri(c,d)+2.5,strcat(tmpNumber,"%"),'FontSize',15,'HorizontalAlignment','center');
            tmpPoint(d,1) = 0.775+(c-1)*0.225+d-1;
            tmpPoint(d,2) = pdAttri(d,c);
            plot(tmpPoint(d,1),tmpPoint(d,2),'o',"Color",[0 0 0],'MarkerSize',10,...
                'MarkerEdgeColor',[0 0 0],'MarkerFaceColor',tierColor(3*c-2:3*c));
        end
        plot([tmpPoint(1,1) tmpPoint(2,1)],[tmpPoint(1,2) tmpPoint(2,2)],'LineWidth',1.5,'Color',tierColor(3*c-2:3*c));
        plot([tmpPoint(2,1) tmpPoint(3,1)],[tmpPoint(2,2) tmpPoint(3,2)],'LineWidth',1.5,'Color',tierColor(3*c-2:3*c));
    end
  
    set(gca,'xticklabels',pdStageDes);
    set(gca,'FontSize',15);
    ylim([0 105]);
    ytickformat("percentage");
    xlabel('PD Stages');
    ylabel('Portion in each stage(%)');
    legend(['<= ',num2str(pdTier(1))],[num2str(pdTier(1)),' < && < ',num2str(pdTier(2))],['>= ',num2str(pdTier(2))]);

    title(pdTitle);
    if(isSave == 1)
        saveas(gcf,strcat(pdTitle,".png"));
    end
    hold off;
end
