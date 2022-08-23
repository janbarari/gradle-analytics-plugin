
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
