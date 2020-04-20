function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Type";
    txtLang[1].innerHTML = "Name";
    txtLang[2].innerHTML = "Wert";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Schaltfl\u00E4che";
    optLang[1].text = "Senden";
    optLang[2].text = "R\u00FCcksetzen";
        
    document.getElementById("btnCancel").value = "Abbrechen";
    document.getElementById("btnInsert").value = "Einf\u00FCgen"; //"insert";
    document.getElementById("btnApply").value = "\u00DCbernehmen"; //"apply";
    document.getElementById("btnOk").value = " OK ";
    }
function writeTitle()
    {
    document.write("<title>"+"Schaltfl\u00E4che"+"</title>")
    }