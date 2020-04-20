function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Naam";
    txtLang[1].innerHTML = "Grootte";
    txtLang[2].innerHTML = "Meerdere selecties";
    txtLang[3].innerHTML = "Waarden";
    
    document.getElementById("btnAdd").value = "toevoegen";
    document.getElementById("btnUp").value = "op";
    document.getElementById("btnDown").value = "neer";
    document.getElementById("btnDel").value = "wissen";
    document.getElementById("btnCancel").value = "annuleren";
    document.getElementById("btnInsert").value = "invoegen";
    document.getElementById("btnApply").value = "toepassen";
    document.getElementById("btnOk").value = " ok ";
    }
function writeTitle()
    {
    document.write("<title>Lijst</title>")
    }