function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Name";
    txtLang[1].innerHTML = "Gr&ouml;&szlig;e";
    txtLang[2].innerHTML = "Mehrfachauswahl";
    txtLang[3].innerHTML = "Werte";

    document.getElementById("btnAdd").value = "einf\u00FCgen";//"  add  ";
    document.getElementById("btnUp").value = " rauf ";
    document.getElementById("btnDown").value = " runter ";
    document.getElementById("btnDel").value = "l\u00F6schen";//"  del  ";
    document.getElementById("btnCancel").value = "Abbrechen";
    document.getElementById("btnInsert").value = "Einf\u00FCgen"; //"insert";
    document.getElementById("btnApply").value = "\u00DCbernehmen"; //"apply";
    document.getElementById("btnOk").value = " OK ";
    }
function writeTitle()
    {
    document.write("<title>Liste</title>")
    }