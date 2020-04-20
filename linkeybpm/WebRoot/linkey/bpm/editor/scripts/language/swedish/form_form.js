function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Namn";
    txtLang[1].innerHTML = "H\u00E4ndelse";
    txtLang[2].innerHTML = "Metod";
        
    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnInsert").value = "Infoga";
    document.getElementById("btnApply").value = "Verkst\u00E4ll";
    document.getElementById("btnOk").value = " OK ";
    }
function writeTitle()
    {
    document.write("<title>" + "Formul\u00E4r" + "</title>")
    }