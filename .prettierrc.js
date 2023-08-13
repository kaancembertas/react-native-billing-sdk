module.exports = {
    tabWidth: 4,
    bracketSpacing: true,
    jsxBracketSameLine: true,
    singleQuote: true,
    trailingComma: 'all',
    printWidth: 120,
    overrides: [
        {
            files: ['index.ts'],
            options: {
                printWidth: 1000,
            },
        },
    ],
};
