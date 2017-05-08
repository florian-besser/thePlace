
export function generateRandomBoard(width, height) {
    let board = [];
    for (let x = 0; x < width; x++) {
        for (let y = 0; y < height; y++) {
            board.push({
                x,
                y,
                color: getRandomColor()
            });
        }
    }
    return board;
}

function getRandomColor() {
    const r = randomInt(0, 255).toString(16);
    const g = randomInt(0, 255).toString(16);
    const b = randomInt(0, 255).toString(16);

    return `${r}${g}${b}`;
}

function randomInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}