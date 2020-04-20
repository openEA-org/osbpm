function loadTxt()
    {
    document.getElementById("txtLang").innerHTML = "Color";
    document.getElementById("btnCancel").value = "cancel";
    document.getElementById("btnOk").value = " ok ";
    }
function getTxt(s)
	{
	switch(s)
		{
		case "No Border": return "No Border";
		case "Outside Border": return "Outside Border";
		case "Left Border": return "Left Border";
		case "Top Border": return "Top Border";
		case "Right Border": return "Right Border";
		case "Bottom Border": return "Bottom Border";
		case "Pick": return "Pick";
		case "Custom Colors": return "Custom Colors";
		case "More Colors...": return "More Colors...";
		default: return "";
		}
	}
function writeTitle()
	{
	document.write("<title>Borders</title>")
	}