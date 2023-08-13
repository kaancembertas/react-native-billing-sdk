module.exports = {
    root: true,
    parserOptions: {
        project: './tsconfig.json',
    },
    env: {
        jest: true,
    },
    extends: ['@react-native-community', 'plugin:@typescript-eslint/recommended'],
    parser: '@typescript-eslint/parser',
    plugins: ['prettier', 'react-hooks', '@typescript-eslint'],
    rules: {
        'prettier/prettier': [
            'warn',
            {
                endOfLine: 'auto',
            },
        ],
        'no-restricted-imports': [
            'error',
            {
                patterns: ['.ts', '.tsx', '.d.ts'],
            },
        ],
        'react-native/no-inline-styles': ['error'],
        'react-hooks/exhaustive-deps': ['off'],
        semi: ['off'],
        '@typescript-eslint/no-empty-function': 'off',
        // Disabling the base rules as it can report incorrect errors
        'no-unused-vars': 'off',
        '@typescript-eslint/no-unused-vars': ['error'],
        'no-shadow': 'off',
        '@typescript-eslint/no-shadow': ['error'],
        '@typescript-eslint/ban-ts-comment': 'off',
        // -------------------------
        'no-extra-boolean-cast': 'off',
    },
};
