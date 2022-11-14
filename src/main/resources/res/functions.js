
function getRandomColor() {
    const letters = '0123456789ABCDEF';
    var color = '#';
    for (var i = 0; i < 6; i++) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
}

function shadeColor(color, amount) {
  return '#' + color.replace(/^#/, '')
  .replace(/../g, color => ('0'+Math.min(255, Math.max(0, parseInt(color, 16) + amount))
  .toString(16))
  .substr(-2));
}

let resetFlag = 0
let randomColor = getRandomColor()

function getColor(single = false) {
  if(single) {
    return getRandomColor();
  }
  if(resetFlag > 1) {
    resetFlag = 0;
    randomColor = getRandomColor();
  }
  resetFlag++;
  return randomColor;
}

var isDarkMode = false

function toggleTheme() {
  var element = document.body;
  element.classList.toggle("dark-mode");

  if(!isDarkMode) {
    isDarkMode = true
    document.getElementById("toggleThemeButton").classList.remove('dark');
    document.getElementById("toggleThemeButton").classList.add('white');
    document.getElementById("toggleThemeButton").classList.remove('bi-moon-stars-fill');
    document.getElementById("toggleThemeButton").classList.add('bi-brightness-high-fill');
  } else {
    isDarkMode = false
    document.getElementById("toggleThemeButton").classList.remove('white');
    document.getElementById("toggleThemeButton").classList.add('dark');
    document.getElementById("toggleThemeButton").classList.remove('bi-brightness-high-fill');
    document.getElementById("toggleThemeButton").classList.add('bi-moon-stars-fill');
  }
}
