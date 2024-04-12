const { XMLParser, XMLBuilder } = require('fast-xml-parser');

const options = {
  ignoreAttributes: false,
  preserveOrder: true,
  commentPropName: "#comment",
  format: true,
  indentBy: "    ",
  suppressEmptyNode: true
};

function getVersionElt(result) {
  return result
    .find(element => element.hasOwnProperty('project'))['project']
    .find(element => element.hasOwnProperty('version'))['version']
    .find(element => element.hasOwnProperty('#text'));
}

module.exports = {
  readVersion: contents => {
    const result = new XMLParser(options).parse(contents);
    return getVersionElt(result)['#text'];
  },
  writeVersion: (contents, version) => {
    const result = new XMLParser(options).parse(contents);
    getVersionElt(result)['#text'] = version;
    return new XMLBuilder(options).build(result);
  }
};
