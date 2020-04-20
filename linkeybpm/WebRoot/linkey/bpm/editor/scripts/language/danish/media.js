function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Sti";
    txtLang[1].innerHTML = "Bredde";
    txtLang[2].innerHTML = "H\u00F8jde";   
    txtLang[3].innerHTML = "Auto Start";
    txtLang[4].innerHTML = "Vis kontrolknapper";
    txtLang[5].innerHTML = "Vis Statuslinie";   
    txtLang[6].innerHTML = "Vis titel";
    txtLang[7].innerHTML = "Loop video";    

    document.getElementById("btnCancel").value = "Annuller";
    document.getElementById("btnInsert").value = "Inds\u00E6t";
    document.getElementById("btnApply").value = "Opdater";
    document.getElementById("btnOk").value = " Ok ";
    }
function writeTitle()
    {
    document.write("<title>"+"Inds\u00E6t mediefil"+"</title>")
    }