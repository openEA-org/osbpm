function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Source";
    txtLang[1].innerHTML = "Width";
    txtLang[2].innerHTML = "Height";    
    txtLang[3].innerHTML = "Auto Start";    
    txtLang[4].innerHTML = "Show Controls";
    txtLang[5].innerHTML = "Show Status Bar";   
    txtLang[6].innerHTML = "Show Display";
    txtLang[7].innerHTML = "Auto Rewind";   

    document.getElementById("btnCancel").value = "annuleren";
    document.getElementById("btnInsert").value = "invoegen";
    document.getElementById("btnApply").value = "toepassen";
    document.getElementById("btnOk").value = " ok ";
    }
function writeTitle()
    {
    document.write("<title>Media</title>")
    }
