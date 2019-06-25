function f = attriReg(pdStageCol,pdSheet,attriCol)
    pdAttri = zeros(52,3);
    tmpC = [1 1 1];
    for c = 1:52
        for d = 1:3
            if(pdStageCol(c) == d)
                pdAttri(tmpC(d),d) = pdSheet(c,attriCol);
                tmpC(d) = tmpC(d) + 1;
            end
        end
    end
    
    f = pdAttri;
end