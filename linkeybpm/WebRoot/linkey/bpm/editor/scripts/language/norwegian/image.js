function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Sti";
    txtLang[1].innerHTML = "Alternativ Tekst";
    txtLang[2].innerHTML = "Avstand";
    txtLang[3].innerHTML = "Justering";
    txtLang[4].innerHTML = "Topp";
    txtLang[5].innerHTML = "Ramme";
    txtLang[6].innerHTML = "Nederst";
    txtLang[7].innerHTML = "Bredde";
    txtLang[8].innerHTML = "Venstre";
    txtLang[9].innerHTML = "H\u00F8yde";
    txtLang[10].innerHTML = "H&#248;yre";
    
    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Abs. nederst";
    optLang[1].text = "Abs. midten";
    optLang[2].text = "Grunnlinje";
    optLang[3].text = "Nederst";
    optLang[4].text = "Venstre";
    optLang[5].text = "Midten";
    optLang[6].text = "H\u00F8yre";
    optLang[7].text = "Topp av tekst";
    optLang[8].text = "&Oslash;verst";
 
    document.getElementById("btnBorder").value = " Ramme typografi ";
    document.getElementById("btnReset").value = "reset"
    
    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnInsert").value = "Sett inn"
    document.getElementById("btnApply").value = "Oppdater";
    document.getElementById("btnOk").value = " Ok ";
    }
function writeTitle()
    {
    document.write("<title>Bilde</title>")
    }