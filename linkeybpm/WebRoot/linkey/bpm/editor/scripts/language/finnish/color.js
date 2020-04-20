function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Web Pallete";
    txtLang[1].innerHTML = "Named Colors";
    txtLang[2].innerHTML = "216 Web Safe";
    txtLang[3].innerHTML = "New";
    txtLang[4].innerHTML = "Current";
    txtLang[5].innerHTML = "Custom colors";
    
    document.getElementById("btnAddToCustom").value = "Add to Custom Colors";
    document.getElementById("btnCancel").value = "Peruuta";
    document.getElementById("btnRemove").value = " remove color ";
    document.getElementById("btnApply").value = "K\u00E4yt\u00E4";
    document.getElementById("btnOk").value = " OK ";
    }
function writeTitle()
    {
    document.write("<title>"+"V\u00E4rit"+"</title>")
    }