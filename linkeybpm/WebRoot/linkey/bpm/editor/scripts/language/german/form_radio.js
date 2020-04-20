function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Name";
    txtLang[1].innerHTML = "Wert";
    txtLang[2].innerHTML = "Vorgabewert";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "markiert"
    optLang[1].text = "unmarkiert"
    
    document.getElementById("btnCancel").value = "Abbrechen";
    document.getElementById("btnInsert").value = "Einf\u00FCgen"; //"insert";
    document.getElementById("btnApply").value = "\u00DCbernehmen"; //"apply";
    document.getElementById("btnOk").value = " OK ";
    }
function writeTitle()
    {
    document.write("<title>Optionsfeld</title>")
    }