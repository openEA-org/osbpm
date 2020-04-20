function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Dateipfad";
    txtLang[1].innerHTML = "Lesezeichen";
    txtLang[2].innerHTML = "Ziel";
    txtLang[3].innerHTML = "Titel";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "dieses Dokument"
    optLang[1].text = "neues Fenster"
    optLang[2].text = "Elterndokument"
    
    document.getElementById("btnCancel").value = "Abbrechen";
    document.getElementById("btnInsert").value = "Einf\u00FCgen"; //"insert";
    document.getElementById("btnApply").value = "\u00DCbernehmen"; //"apply";
    document.getElementById("btnOk").value = " OK ";
    }
function writeTitle()
    {
    document.write("<title>"+"Verkn\u00FCpfung"+"</title>")
    }