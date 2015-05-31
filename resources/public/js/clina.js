/**
 * Created by zjh on 15-5-31.
 */

function diffUsingJS(oldTextId, newTextId, outputId) {
    // get the baseText and newText values from the two textboxes, and split them into lines
    var oldText = document.getElementById(oldTextId).value;
    if (oldText == '') {
        var oldLines = [];
    } else {
        var oldLines = difflib.stringAsLines(oldText);
    }

    var newText = document.getElementById(newTextId).value
    if (newText == '') {
        var newLines = [];
    } else {
        var newLines = difflib.stringAsLines(newText);
    }

    // create a SequenceMatcher instance that diffs the two sets of lines
    var sm = new difflib.SequenceMatcher(oldLines, newLines);

    // get the opcodes from the SequenceMatcher instance
    // opcodes is a list of 3-tuples describing what changes should be made to the base text
    // in order to yield the new text
    var opcodes = sm.get_opcodes();
    var diffoutputdiv = document.getElementById(outputId);
    while (diffoutputdiv.firstChild) diffoutputdiv.removeChild(diffoutputdiv.firstChild);

    // build the diff view and add it to the current DOM
    diffoutputdiv.appendChild(diffview.buildView({
        baseTextLines: oldLines,
        newTextLines: newLines,
        opcodes: opcodes,
        contextSize: 4,
        viewType: 1
    }));
}