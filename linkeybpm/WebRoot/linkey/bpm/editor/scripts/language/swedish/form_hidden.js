function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Namn";
    txtLang[1].innerHTML = "V\u00E4rde";
    
    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnInsert").value = "Infoga";
    document.getElementById("btnApply").value = "Verkst\u00E4ll";
    document.getElementById("btnOk").value = " OK ";
    }
function writeTitle()
    {
    document.write("<title>" + "Dolt f\u00E4lt" + "</title>")
    }
