// utils/find-duplicate/result.txt 파일에서 중복된 숫자를 검증한다.

const fs = require('fs')

function main() {
    const result = fs.readFileSync('result.txt', 'utf8')

    const numbers = result.split('\n').map(Number)

    const duplicates = numbers.filter((number, index) =>
        numbers.indexOf(number) !== index
    )

    if (duplicates.length > 0) {
        throw new Error(`Duplicates found: ${duplicates.join(', ')}`)
    }
}

main()