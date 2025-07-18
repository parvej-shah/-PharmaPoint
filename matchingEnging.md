### ✅ **Full Query**

```sql
SELECT p.id, p.user_id, p.name, p.address, p.area
FROM medicines m
JOIN pharmacies p ON m.pharmacy_id = p.id
WHERE (m.name LIKE ? OR m.generic_name LIKE ?)
  AND m.quantity > 0
GROUP BY p.id
```

---

#### ✅ `SELECT p.id, p.user_id, p.name, p.address, p.area`

* This tells the database to return columns **only from the `pharmacies` table** (`p`):

    * `id` → Pharmacy ID
    * `user_id` → The user account (owner) associated with this pharmacy
    * `name`, `address`, `area` → Basic info about the pharmacy

We're not selecting any columns from `medicines` — just using it to **filter pharmacies** that have a specific medicine in stock.

---

#### ✅ `FROM medicines m`

* The **`medicines` table** is aliased as `m`
* We're starting from `medicines` because we want to **filter based on medicine names and stock**

---

#### ✅ `JOIN pharmacies p ON m.pharmacy_id = p.id`

* This joins the `medicines` and `pharmacies` tables:

    * `m.pharmacy_id` is a foreign key referencing `pharmacies.id`
    * This way, you can get **pharmacy information** based on medicines they stock

---

#### ✅ `WHERE (m.name LIKE ? OR m.generic_name LIKE ?)`

* This filters for medicines where:

    * Either the **brand name** (`m.name`)
    * Or the **generic name** (`m.generic_name`)
    * **matches** the user’s search keyword (using `LIKE '%keyword%'`)

This allows partial search — for example, typing `"para"` would match `"Paracetamol"` or `"Paracip"`.

---

#### ✅ `AND m.quantity > 0`

* Ensures the medicine is actually **in stock**
* Only pharmacies with **available quantity** will be returned

---

#### ✅ `GROUP BY p.id`

* Ensures each **pharmacy appears only once** in the result
* Even if a pharmacy stocks **multiple medicines** matching the keyword, this avoids duplicates

---

### ✅ Purpose Summary:

This query finds all pharmacies **that currently have** a medicine matching a keyword (brand or generic) and have at least one unit in stock.

---

### Visualization

```
[medicines]         [pharmacies]
+-------------+     +-------------------+
| id          |     | id                |
| name        | --> | name              |
| genericName |     | address           |
| quantity>0  |     | area              |
| pharmacy_id |     | user_id           |
+-------------+     +-------------------+
```

---