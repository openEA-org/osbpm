function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Namn";
    txtLang[1].innerHTML = "Storlek";
    txtLang[2].innerHTML = "Flerval";
    txtLang[3].innerHTML = "V\u00E4rden";
    
    document.getElementById("btnAdd").value = "  L\u00E4gg till  ";
    document.getElementById("btnUp").value = "  Upp  ";
    document.getElementById("btnDown").value = "  Ner  ";
    document.getElementById("btnDel").value = "  Ta bort  ";
    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnInsert").value = "Infoga";
    document.getElementById("btnApply").value = "Verkst\u00E4ll";
    document.getElementById("btnOk").value = " OK ";
    }
function writeTitle()
    {
    document.write("<title>Meny</title>")
    }