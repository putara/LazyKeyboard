# Introduction
One of the smallest and the dumbest soft keyboard apps that can quickly autofill username and password.

## Limitations

* _No security_ : All passwords are stored in a plain text file
* Does _not_ support landscape mode because I'm too lazy

## Requirements

- Android 9.0 Pie or later

## Usage

1. Launch `Teretere` app
2. Turn on the first two options i.e. grant storage permission and enable keyboard
3. Create `teretere.yml` on the internal storage root directory
4. Open your test app
5. Switch keyboard to `Teretere`
6. Click a button on the entry
7. The keyboard will enter `username <next> password <go>` then switch back to the previous keyboard

## YAML file format
Save as `teretere.yml` and press the reload button on the keyboard.

* `delay` : specifies the amount of waiting time after sending `<next>` in milliseconds
* `password` : specifies default password
* `members` : specifies each member's credentials
  * `name` : short name
  * `description` : short description
  * `email` : member's email address
  * `password` : member's password (optional: the default password is used if omitted)

### Example

```yaml
delay: 100
password: P@a$$w0rd
members:
  - name        : Admin
    email       : admin@example.com
    password    : S33kr!t4Admin
    description : Website administrator
  - name        : Manager
    email       : manager-1@example.com
    description : Website manager
  - name        : User
    email       : user-9@example.com
    description : Normal user
```

## License
Released under the [WTFPL](http://www.wtfpl.net/about/).
