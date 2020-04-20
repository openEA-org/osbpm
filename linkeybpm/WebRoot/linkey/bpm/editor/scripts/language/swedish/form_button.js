function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Typ";
    txtLang[1].innerHTML = "Namn";
    txtLang[2].innerHTML = "V\u00E4rde";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Knapp"
    optLang[1].text = "Skicka"
    optLang[2].text = "Rensa"
        
    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnInsert").value = "Infoga";
    document.getElementById("btnApply").value = "Verkst\u00E4ll";
    document.getElementById("btnOk").value = " OK ";
    }
function writeTitle()
    {
    document.write("<title>Knapp</title>")
    }