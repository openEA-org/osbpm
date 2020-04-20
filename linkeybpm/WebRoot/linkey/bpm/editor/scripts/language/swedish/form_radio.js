function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Namn";
    txtLang[1].innerHTML = "V\u00E4rde";
    txtLang[2].innerHTML = "F\u00F6rvalt";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Valt"
    optLang[1].text = "Ej valt"
    
    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnInsert").value = "Infoga";
    document.getElementById("btnApply").value = "Verkst\u00E4ll";
    document.getElementById("btnOk").value = " OK ";
    }
function writeTitle()
    {
    document.write("<title>Radio knapp</title>")
    }