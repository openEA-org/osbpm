function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Web Pallete";
    txtLang[1].innerHTML = "benannte Farben";
    txtLang[2].innerHTML = "216 Web sicher";
    txtLang[3].innerHTML = "Neu";
    txtLang[4].innerHTML = "Aktuell";
    txtLang[5].innerHTML = "Benutzerfarben";

    document.getElementById("btnAddToCustom").value = "zu Benutzerfarben hinzuf\u00fcgen";
    document.getElementById("btnCancel").value = "Abbrechen";
    document.getElementById("btnRemove").value = " Farbe entfernen ";
    document.getElementById("btnApply").value = "\u00DCbernehmen"; //"apply";
    document.getElementById("btnOk").value = " OK ";
    }
function writeTitle()
    {
    document.write("<title>Farben</title>")
    }