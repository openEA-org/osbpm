function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Type";
    txtLang[1].innerHTML = "Name";
    txtLang[2].innerHTML = "Gr&ouml;&szlig;e";
    txtLang[3].innerHTML = "Max L&auml;nge";
    txtLang[4].innerHTML = "Zeilen";
    txtLang[5].innerHTML = "Vorgabewert";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Text einzeilig"
    optLang[1].text = "Text mehrzeilig"
    optLang[2].text = "Passwort"
    
    document.getElementById("btnCancel").value = "Abbrechen";
    document.getElementById("btnInsert").value = "Einf\u00FCgen"; //"insert";
    document.getElementById("btnApply").value = "\u00DCbernehmen"; //"apply";
    document.getElementById("btnOk").value = " OK ";
    }
function writeTitle()
    {
    document.write("<title>Textfeld</title>")
    }