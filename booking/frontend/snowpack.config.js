module.exports = {
  devOptions: {
    port: 4000,
    open: "none",
  },
  mount: {
    public: '/',
    src: '/_dist_',
  },
  plugins: [
    '@snowpack/plugin-react-refresh',
    '@snowpack/plugin-dotenv',
    "snowpack-plugin-sass",
    ["@snowpack/plugin-optimize", {target: 'es2015'}],
    ['@snowpack/plugin-run-script', {cmd: 'tsc --noEmit', watch: '$1 --watch'}],
  ],
};
