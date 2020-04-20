function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Sti";
    txtLang[1].innerHTML = "Bredde";
    txtLang[2].innerHTML = "H\u00F8yde"; 
    txtLang[3].innerHTML = "Auto Start";
    txtLang[4].innerHTML = "Vis kontrollknapper";
    txtLang[5].innerHTML = "Vis Statuslinje";   
    txtLang[6].innerHTML = "Vis tittel";
    txtLang[7].innerHTML = "Repeter video"; 

    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnInsert").value = "Sett inn";
    document.getElementById("btnApply").value = "Oppdater";
    document.getElementById("btnOk").value = " Ok ";
    }
function writeTitle()
    {
    document.write("<title>"+"Sett inn mediefil"+"</title>")
    }