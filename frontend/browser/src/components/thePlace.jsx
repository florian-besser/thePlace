import * as React from "react";
import {bindActionCreators} from "redux";
import {connect} from "react-redux";
import {selectPixel} from "../actions";
import {ColorSelector} from "./ColorPicker";
import {drawBoard, getPixelCoordinates, PIXEL_SIZE} from "../lib/canvas";


function ThePlaceComponent({board, selectPixel}) {
    const hasLoaded = board.colors.length > 0;

    return (
        <div>
            {hasLoaded && <PlaceCanvas colors={board.colors} selectPixel={selectPixel}/>}
            <ColorSelector />
        </div>
    );
}

class PlaceCanvas extends React.Component {

    componentDidMount() {
        this.canvas = document.getElementById('placeCanvas');
        drawBoard(this.canvas, this.props.colors);
    }

    onClick(event) {
        const clickedCoords = getPixelCoordinates(this.canvas, event.pageX, event.pageY);

        const {x, y} = clickedCoords;
        this.props.selectPixel(x, y, this.props.colors[y][x]);
    }

    shouldComponentUpdate() {
        return false;
    }

    render() {
        const {colors} = this.props;

        const width = colors[0] ? colors[0].length : 0;
        const height = colors.length;
        return (
            <canvas
                className='place'
                id='placeCanvas'
                width={width * PIXEL_SIZE}
                height={height * PIXEL_SIZE}
                onClick={(event) => this.onClick(event)}
            />
        );
    }
}

export const ThePlace = connect(
    (state, ownProps) => ({
        board: state.board
    }),
    dispatch => bindActionCreators({selectPixel}, dispatch)
)(ThePlaceComponent);
