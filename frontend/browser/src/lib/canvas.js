
export const PIXEL_SIZE = 1;

export function drawBoard(canvas, colors) {
    if (!canvas) {
        return;
    }

    colors.forEach((line, y) => {
        line.forEach((color, x) => {
            drawNewPixel(canvas, x, y, color);
        });
    });
}

export function drawNewPixel(canvas, x, y, color) {
    if (!canvas) {
        return;
    }

    const ctx = canvas.getContext('2d');
    ctx.fillStyle = color;
    ctx.fillRect(x * PIXEL_SIZE, y * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
}

export function getPixelCoordinates(canvas, clickX, clickY) {
    const x = (clickX - canvas.offsetLeft) / PIXEL_SIZE;
    const y = (clickY - canvas.offsetTop) / PIXEL_SIZE;

    return {
        x: Math.floor(x),
        y: Math.floor(y)
    };
}
