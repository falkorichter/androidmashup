    <td><?php echo link_to($extratype->getIdExtratype() ? $extratype->getIdExtratype() : __('-'), 'extratype/edit?id_extratype='.$extratype->getIdExtratype()) ?></td>
    <td><?php echo $extratype->getName() ?></td>
      <td><?php echo $extratype->getDescription() ?></td>
      <td><?php echo $extratype->getJavaRetrieveCommand() ?></td>
  