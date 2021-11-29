# ARmoney
Плагин майнкрафт для добавления в игру виртуальной валюты "АР". (требует плагин Vault)  
1АР = 1АЛМАЗНАЯ РУДА (любого типа)

### Комманды:
* __/ar sell [TAB] [кол.во]__ - _Положить на счёт определённое количество руды._
* __/ar get [TAB] [кол.во]__ - _Снять со счёта определённое количество руды._
* __/ar autosell [enable/disable]__ - _Включить/Выключить автопродажу добываемой руды._
* __/ar pickupsell [enable/disable]__ - _Включить/Выключить автопродажу подбираемой руды._
* __/ar reload__ - _Выполнить перезагрузку плагина._ (требует права armoney.reload)

### Конфиг по умолчанию:
'''
messages:
   main:
      ar_positive: '§fКоличество &aАР §fдолжно быть §aположительным!'
      lack_of_ore: '§cНехватает руды! §fНужно ещё §a{count} АР&f!'
      sell_success: '§fВы положили на счёт §a{count} АР'
      lack_of_ar: '§cНехватает денег! §fНужно ещё §a{count} АР'
      get_success: '§fВы вывели со счёта §a{count} АР'
      be_a_number: '§fКоличество &aАР §fдолжно быть числом!'
      auto_add: '§fВам был автоматически начислен §a{count} АР'
      autosell_on: '§fВы §aвключили §fавтопродажу добываемой руды.'
      autosell_off: '§fВы §cвыключили §fавтопродажу добываемой руды.'
      pickupsell_on: '§fВы §aвключили §fавтопродажу подбираемой руды.'
      pickupsell_off: '§fВы §cвыключили §fавтопродажу подбираемой руды.'  
   help:
      sell: '§fПоложить на счёт определённое количество руды.'
      get: '§fСнять со счёта определённое количество руды.'
      autosell: '§aВключить§7/§cВыключить §fавтопродажу добываемой руды.'
      pickupsell: '§aВключить§7/§cВыключить §fавтопродажу подбираемой руды.'
      bal: '§fУзнать количество &aАР &fна счёту.'
      pay: '§fОтправить игроку определённое количество &aАР§f.'
      reload: '§fВыполнить перезагрузку плагина.'
   admin:
      prefix: '&7[&5ARmoney&7] '
      unknown_command: '§cНеизвестная команда! §fИспользуй §5/ar help&f.'
      dont_have_permission: '§fУ вас §cнет прав §fна выполнение этой комманды!'
      config_reloaded: '§aКонфиг перезагружен!'
'''
