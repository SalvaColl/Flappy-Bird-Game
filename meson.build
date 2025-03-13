project('flappybird', 'java')

sources = [
  'src/FlappyBird.java',
  'src/App.java',
]

resources = structured_sources([],{ 'images': files(
  'src/images/bottompipe.png',
  'src/images/flappybird.png',
  'src/images/flappybirdbg.png',
  'src/images/toppipe.png',
)})

game = jar(
  meson.project_name(),
  sources,
  main_class: 'App',
  java_resources: resources
)