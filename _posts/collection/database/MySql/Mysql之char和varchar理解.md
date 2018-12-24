

<table>
    <colgroup>
        <col>
        <col>
        <col>
        <col>
        <col>
    </colgroup>
    <thead>
    <tr>
        <th scope="col">Value</th>
        <th scope="col"><code class="literal">CHAR(4)</code></th>
        <th scope="col">Storage Required</th>
        <th scope="col"><code class="literal">VARCHAR(4)</code></th>
        <th scope="col">Storage Required</th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <td scope="row"><code class="literal">''</code></td>
        <td><code class="literal">'&nbsp;&nbsp;&nbsp;&nbsp;'</code></td>
        <td>4 bytes</td>
        <td><code class="literal">''</code></td>
        <td>1 byte</td>
    </tr>
    <tr>
        <td scope="row"><code class="literal">'ab'</code></td>
        <td><code class="literal">'ab&nbsp;&nbsp;'</code></td>
        <td>4 bytes</td>
        <td><code class="literal">'ab'</code></td>
        <td>3 bytes</td>
    </tr>
    <tr>
        <td scope="row"><code class="literal">'abcd'</code></td>
        <td><code class="literal">'abcd'</code></td>
        <td>4 bytes</td>
        <td><code class="literal">'abcd'</code></td>
        <td>5 bytes</td>
    </tr>
    <tr>
        <td scope="row"><code class="literal">'abcdefgh'</code></td>
        <td><code class="literal">'abcd'</code></td>
        <td>4 bytes</td>
        <td><code class="literal">'abcd'</code></td>
        <td>5 bytes</td>
    </tr>
    </tbody>
</table>

### 参考文章
* [The CHAR and VARCHAR Types (from dev.mysql.com)](https://dev.mysql.com/doc/refman/5.7/en/char.html)
