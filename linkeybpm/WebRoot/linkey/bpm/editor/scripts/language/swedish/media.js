function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "K\u00E4lla";
    txtLang[1].innerHTML = "Bredd";
    txtLang[2].innerHTML = "H\u00F6jd";  
    txtLang[3].innerHTML = "Starta automatiskt";    
    txtLang[4].innerHTML = "Visa kontroller";
    txtLang[5].innerHTML = "Visa statusf\u00E4lt";   
    txtLang[6].innerHTML = "Visa dsisplay";
    txtLang[7].innerHTML = "Automatisk \u00E5terspelning";   

    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnInsert").value = "Infoga";
    document.getElementById("btnApply").value = "Verkst\u00E4ll";
    document.getElementById("btnOk").value = " OK ";
    }
function writeTitle()
    {
    document.write("<title>Media</title>")
    }
